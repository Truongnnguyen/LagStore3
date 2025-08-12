/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lags.controller;

/**
 *
 * @author admin
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class MyChartPanel extends ChartPanel {
    public MyChartPanel() {
        super(createSampleChart());
    }

    private static JFreeChart createSampleChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // dữ liệu mẫu
        dataset.addValue(1, "Series", "A");
        dataset.addValue(4, "Series", "B");
        dataset.addValue(3, "Series", "C");
        return ChartFactory.createBarChart("Mẫu Chart", "X", "Y", dataset);
    }
}