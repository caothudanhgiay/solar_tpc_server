package com.example.solar_tpc_server.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Nén ảnh upload: xoay đúng theo EXIF, resize cạnh dài tối đa MAX_DIMENSION,
 * xuất JPEG (hoặc PNG nếu ảnh có nền trong suốt).
 */
@Slf4j
public final class TsoImageUtil {

    private TsoImageUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final int MAX_DIMENSION = 1920;
    public static final float JPEG_QUALITY = 0.8f;
    private static final Set<String> COMPRESSIBLE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png");

    public record TsoCompressedImage(byte[] data, String extension) {}

    /**
     * Trả về ảnh đã nén, hoặc null nếu định dạng không hỗ trợ / đọc lỗi / nén xong không nhỏ hơn
     * → caller giữ nguyên file gốc.
     */
    public static TsoCompressedImage compress(byte[] original, String extension) {
        if (original == null || extension == null || !COMPRESSIBLE_EXTENSIONS.contains(extension.toLowerCase())) {
            return null;
        }
        try {
            BufferedImage source = readSubsampled(original);
            if (source == null) return null;

            boolean hasAlpha = source.getColorModel().hasAlpha();
            int orientation = readExifOrientation(original);
            BufferedImage result = transform(source, orientation, hasAlpha);

            TsoCompressedImage compressed = hasAlpha
                    ? new TsoCompressedImage(writePng(result), ".png")
                    : new TsoCompressedImage(writeJpeg(result), ".jpg");
            return compressed.data().length < original.length ? compressed : null;
        } catch (IOException | RuntimeException e) {
            log.warn("Image compression skipped, keep original file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Giải mã ảnh có subsampling: ảnh rất lớn (vd 8000x6000) chỉ đọc mỗi n pixel 1 lần sao cho cạnh dài
     * vẫn ≥ 2 * MAX_DIMENSION → giữ độ nét khi resize nhưng không phải giữ cả ảnh gốc trong heap.
     */
    static BufferedImage readSubsampled(byte[] data) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
            if (iis == null) return null;
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) return null;
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                int longSide = Math.max(reader.getWidth(0), reader.getHeight(0));
                int step = Math.max(1, longSide / (MAX_DIMENSION * 2));
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceSubsampling(step, step, 0, 0);
                return reader.read(0, param);
            } finally {
                reader.dispose();
            }
        }
    }

    /** Xoay/lật theo EXIF orientation (1-8) và thu nhỏ trong 1 lần vẽ. */
    static BufferedImage transform(BufferedImage src, int orientation, boolean hasAlpha) {
        int w = src.getWidth();
        int h = src.getHeight();
        boolean swap = orientation >= 5 && orientation <= 8;
        int ow = swap ? h : w;
        int oh = swap ? w : h;
        double scale = Math.min(1.0, (double) MAX_DIMENSION / Math.max(ow, oh));
        int tw = Math.max(1, (int) Math.round(ow * scale));
        int th = Math.max(1, (int) Math.round(oh * scale));

        AffineTransform orient = new AffineTransform();
        switch (orientation) {
            case 2 -> { orient.translate(w, 0); orient.scale(-1, 1); }
            case 3 -> { orient.translate(w, h); orient.rotate(Math.PI); }
            case 4 -> { orient.translate(0, h); orient.scale(1, -1); }
            case 5 -> { orient.rotate(Math.PI / 2); orient.scale(1, -1); }
            case 6 -> { orient.translate(h, 0); orient.rotate(Math.PI / 2); }
            case 7 -> { orient.translate(h, w); orient.rotate(-Math.PI / 2); orient.scale(1, -1); }
            case 8 -> { orient.translate(0, w); orient.rotate(-Math.PI / 2); }
            default -> { }
        }
        AffineTransform full = AffineTransform.getScaleInstance(scale, scale);
        full.concatenate(orient);

        BufferedImage out = new BufferedImage(tw, th, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!hasAlpha) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, tw, th);
            }
            g.drawImage(src, full, null);
        } finally {
            g.dispose();
        }
        return out;
    }

    /** Đọc tag Orientation (0x0112) trong APP1/Exif của JPEG. Không có/lỗi → 1 (giữ nguyên). */
    static int readExifOrientation(byte[] d) {
        try {
            if (d.length < 4 || (d[0] & 0xFF) != 0xFF || (d[1] & 0xFF) != 0xD8) return 1;
            int i = 2;
            while (i + 4 < d.length) {
                if ((d[i] & 0xFF) != 0xFF) return 1;
                int marker = d[i + 1] & 0xFF;
                int len = ((d[i + 2] & 0xFF) << 8) | (d[i + 3] & 0xFF);
                if (marker == 0xDA) return 1; // Start of Scan — hết phần metadata
                if (marker == 0xE1 && d[i + 4] == 'E' && d[i + 5] == 'x' && d[i + 6] == 'i' && d[i + 7] == 'f') {
                    int tiff = i + 10;
                    boolean le = d[tiff] == 'I';
                    int ifd = tiff + readInt(d, tiff + 4, le);
                    int count = readShort(d, ifd, le);
                    for (int k = 0; k < count; k++) {
                        int entry = ifd + 2 + k * 12;
                        if (readShort(d, entry, le) == 0x0112) {
                            int value = readShort(d, entry + 8, le);
                            return value >= 1 && value <= 8 ? value : 1;
                        }
                    }
                    return 1;
                }
                i += 2 + len;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 1;
        }
        return 1;
    }

    private static int readShort(byte[] d, int p, boolean le) {
        return le ? (d[p] & 0xFF) | ((d[p + 1] & 0xFF) << 8)
                  : ((d[p] & 0xFF) << 8) | (d[p + 1] & 0xFF);
    }

    private static int readInt(byte[] d, int p, boolean le) {
        return le ? readShort(d, p, true) | (readShort(d, p + 2, true) << 16)
                  : (readShort(d, p, false) << 16) | readShort(d, p + 2, false);
    }

    private static byte[] writeJpeg(BufferedImage img) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(JPEG_QUALITY);
            writer.write(null, new IIOImage(img, null, null), param);
        } finally {
            writer.dispose();
        }
        return bos.toByteArray();
    }

    private static byte[] writePng(BufferedImage img) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);
        return bos.toByteArray();
    }
}
