package com.ting.shop.bot.image;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname ImageService
 * @Description TODO
 * @Date 2025/1/13 20:40
 * @Author by chenlt
 */
@Service
public class ImageService {


    public File genImage(List<List<Object>> data) throws IOException {
        List<Integer> cellWidths = getWidths(data);
        List<Integer> cellAns = new ArrayList<>();
        cellAns.add(0);
        for (int i = 0; i < cellWidths.size(); i++) {
            cellAns.add(cellAns.get(i) + cellWidths.get(i));
        }

        int cellPadding = 10;
        int cellHeight = 30;
        int tableWidth = cellAns.get(cellAns.size() - 1);
        int tableLeHeight = cellHeight * data.size();
        // 创建图像
        BufferedImage image = new BufferedImage(tableWidth, tableLeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        // 设置字体
        Font font = new Font("Source Han Sans CN", Font.PLAIN, 18);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        // 绘制表格
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {

                int x = cellAns.get(j) + cellPadding;
                int y = i * cellHeight + cellPadding + g2d.getFontMetrics().getAscent();
                //绘制单元格背景
                if (i == 0) {
                    g2d.setColor(new Color(200, 200, 255));//表头背景色
                } else {
                    g2d.setColor(new Color(255, 255, 255)); //单元格背景色
                }
                g2d.fillRect(x - cellPadding, i * cellHeight, cellWidths.get(j), cellHeight);
                //绘制单元格文本
                if (data.get(i).get(j) != null) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(data.get(i).get(j).toString(), x, y);
                }
                //绘制单元格边框
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x - cellPadding, i * cellHeight, cellWidths.get(j), cellHeight);
            }
        }

        g2d.dispose();
        File file = new File("file.png");
        ImageIO.write(image, "png", file);
        return file;
    }

    private List<Integer> getWidths(List<List<Object>> data) {

        List<Integer> ans = new ArrayList<>();
        int l = data.size();
        int r = data.get(0).size();

        for (int i = 0; i < r; ++i) {
            int len = 100;
            for (List<Object> datum : data) {
                if (datum.get(i) != null) {
                    len = Math.max(len, (datum.get(i).toString().length() / 10 + 1) * 180);
                }
            }
            ans.add(len);
        }
        return ans;
    }
}
