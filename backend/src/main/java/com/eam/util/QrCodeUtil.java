package com.eam.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 * Task 10.1.3: 创建二维码工具类
 * Task 10.1.4: 实现二维码生成和解析方法
 */
public class QrCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeUtil.class);
    
    // 二维码默认设置
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String DEFAULT_FORMAT = "PNG";
    private static final String CHARSET = "UTF-8";
    
    // 二维码颜色设置
    private static final Color FOREGROUND_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final String LOGO_FILE_PATH = "/images/logo.png"; // 系统logo文件路径

    /**
     * 生成二维码到Base64字符串
     * @param content 二维码内容
     * @return Base64编码的图片字符串
     */
    public static String generateQrCodeToBase64(String content) {
        return generateQrCodeToBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成二维码到Base64字符串
     * @param content 二维码内容
     * @param width 图片宽度
     * @param height 图片高度
     * @return Base64编码的图片字符串
     */
    public static String generateQrCodeToBase64(String content, int width, int height) {
        try {
            BufferedImage image = generateQrCodeImage(content, width, height);
            
            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            
            return "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            logger.error("生成二维码Base64字符串失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成二维码图片
     * @param content 二维码内容
     * @return BufferedImage对象
     */
    public static BufferedImage generateQrCodeImage(String content) {
        return generateQrCodeImage(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成二维码图片
     * @param content 二维码内容
     * @param width 图片宽度
     * @param height 图片高度
     * @return BufferedImage对象
     */
    public static BufferedImage generateQrCodeImage(String content, int width, int height) {
        try {
            // 创建二维码编码器
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            // 设置二维码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            
            // 生成二维码矩阵
            BitMatrix bitMatrix = qrCodeWriter.encode(content, hints);
            
            // 创建BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            
            // 设置背景色
            graphics.setColor(BACKGROUND_COLOR);
            graphics.fillRect(0, 0, width, height);
            
            // 设置前景色
            graphics.setColor(FOREGROUND_COLOR);
            
            // 计算每个模块的大小
            int moduleWidth = width / bitMatrix.getWidth();
            int moduleHeight = height / bitMatrix.getHeight();
            
            // 绘制二维码
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x * moduleWidth, y * moduleHeight, moduleWidth, moduleHeight);
                    }
                }
            }
            
            graphics.dispose();
            return image;
            
        } catch (Exception e) {
            logger.error("生成二维码图片失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成二维码到文件
     * @param content 二维码内容
     * @param filePath 文件路径
     * @return 是否成功
     */
    public static boolean generateQrCodeToFile(String content, String filePath) {
        return generateQrCodeToFile(content, filePath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成二维码到文件
     * @param content 二维码内容
     * @param filePath 文件路径
     * @param width 图片宽度
     * @param height 图片高度
     * @return 是否成功
     */
    public static boolean generateQrCodeToFile(String content, String filePath, int width, int height) {
        try {
            BufferedImage image = generateQrCodeImage(content, width, height);
            if (image == null) {
                return false;
            }
            
            // 确保目录存在
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // 保存图片
            ImageIO.write(image, "PNG", file);
            logger.info("二维码已生成: {}", filePath);
            return true;
            
        } catch (Exception e) {
            logger.error("生成二维码文件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 生成带logo的二维码
     * @param content 二维码内容
     * @param logoPath logo图片路径
     * @return BufferedImage对象
     */
    public static BufferedImage generateQrCodeWithLogo(String content, String logoPath) {
        return generateQrCodeWithLogo(content, logoPath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成带logo的二维码
     * @param content 二维码内容
     * @param logoPath logo图片路径
     * @param width 图片宽度
     * @param height 图片高度
     * @return BufferedImage对象
     */
    public static BufferedImage generateQrCodeWithLogo(String content, String logoPath, int width, int height) {
        try {
            // 生成基础二维码
            BufferedImage qrImage = generateQrCodeImage(content, width, height);
            if (qrImage == null) {
                return null;
            }
            
            // 读取logo图片
            File logoFile = new File(logoPath);
            if (!logoFile.exists()) {
                logger.warn("Logo文件不存在，生成普通二维码: {}", logoPath);
                return qrImage;
            }
            
            BufferedImage logoImage = ImageIO.read(logoFile);
            
            // 创建合成图片
            Graphics2D graphics = qrImage.createGraphics();
            
            // 计算logo大小（建议为二维码大小的15%-20%）
            int logoWidth = width / 5;
            int logoHeight = logoImage.getHeight() * logoWidth / logoImage.getWidth();
            
            // 计算logo居中位置
            int logoX = (width - logoWidth) / 2;
            int logoY = (height - logoHeight) / 2;
            
            // 绘制logo
            graphics.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, null);
            graphics.dispose();
            
            return qrImage;
            
        } catch (Exception e) {
            logger.error("生成带logo的二维码失败: {}", e.getMessage(), e);
            return generateQrCodeImage(content, width, height);
        }
    }

    /**
     * 解析二维码
     * @param qrCodeImagePath 二维码图片路径
     * @return 二维码内容
     */
    public static String decodeQrCode(String qrCodeImagePath) {
        try {
            File qrCodeFile = new File(qrCodeImagePath);
            if (!qrCodeFile.exists()) {
                logger.error("二维码文件不存在: {}", qrCodeImagePath);
                return null;
            }
            
            BufferedImage qrCodeImage = ImageIO.read(qrCodeFile);
            
            // 创建二维码读取器
            QRCodeReader qrCodeReader = new QRCodeReader();
            
            // 读取二维码
            BinaryBitmap binaryBitmap = new BinaryBitmap(qrCodeImage);
            Result result = qrCodeReader.decode(binaryBitmap);
            
            return result.getText();
            
        } catch (Exception e) {
            logger.error("解析二维码失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析二维码（从Base64字符串）
     * @param base64QrCode Base64编码的二维码字符串
     * @return 二维码内容
     */
    public static String decodeQrCodeFromBase64(String base64QrCode) {
        try {
            // 移除data:image/png;base64,前缀
            String base64Image = base64QrCode.substring(base64QrCode.indexOf(",") + 1);
            
            // 解码Base64
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
            
            // 创建BufferedImage
            BufferedImage qrCodeImage = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
            
            // 创建二维码读取器
            QRCodeReader qrCodeReader = new QRCodeReader();
            
            // 读取二维码
            BinaryBitmap binaryBitmap = new BinaryBitmap(qrCodeImage);
            Result result = qrCodeReader.decode(binaryBitmap);
            
            return result.getText();
            
        } catch (Exception e) {
            logger.error("从Base64解析二维码失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成资产二维码
     * @param assetId 资产ID
     * @param assetCode 资产编码
     * @param assetName 资产名称
     * @return 二维码内容
     */
    public static String generateAssetQrCodeContent(Long assetId, String assetCode, String assetName) {
        StringBuilder content = new StringBuilder();
        content.append("EAM_ASSET"); // 系统标识
        content.append(":").append(assetId);
        content.append(":").append(assetCode);
        content.append(":").append(assetName);
        return content.toString();
    }

    /**
     * 解析资产二维码内容
     * @param qrCodeContent 二维码内容
     * @return 资产信息Map
     */
    public static Map<String, String> parseAssetQrCodeContent(String qrCodeContent) {
        Map<String, String> assetInfo = new HashMap<>();
        
        try {
            if (qrCodeContent == null || !qrCodeContent.startsWith("EAM_ASSET")) {
                return assetInfo;
            }
            
            String[] parts = qrCodeContent.split(":");
            if (parts.length >= 4) {
                assetInfo.put("system", parts[0]);
                assetInfo.put("assetId", parts[1]);
                assetInfo.put("assetCode", parts[2]);
                assetInfo.put("assetName", parts[3]);
            }
            
        } catch (Exception e) {
            logger.error("解析资产二维码内容失败: {}", e.getMessage(), e);
        }
        
        return assetInfo;
    }

    /**
     * 检查二维码内容是否为资产二维码
     * @param content 二维码内容
     * @return 是否为资产二维码
     */
    public static boolean isAssetQrCode(String content) {
        return content != null && content.startsWith("EAM_ASSET");
    }

    /**
     * 获取二维码文件扩展名
     * @return 文件扩展名
     */
    public static String getQrCodeFileExtension() {
        return "." + DEFAULT_FORMAT.toLowerCase();
    }
}