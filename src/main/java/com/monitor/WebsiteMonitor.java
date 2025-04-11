package com.monitor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WebsiteMonitor {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<MonitorTarget, Set<Article>> previousArticlesMap = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        // 创建默认监控目标
        List<MonitorTarget> targets = new ArrayList<>();
        targets.add(new MonitorTarget(
            ".list-content ul li",
            "a",
            "span",
            5
        ));

        // 这里可以添加更多监控目标
        // targets.add(new MonitorTarget(...));

        System.out.println("网站监控程序启动...");
        System.out.println("启动时间：" + LocalDateTime.now().format(formatter));
        System.out.println("----------------------------------------");

        // 为每个监控目标启动单独的监控线程
        for (MonitorTarget target : targets) {
            startMonitoring(target);
        }
    }

    private static void startMonitoring(MonitorTarget target) {
        executorService.submit(() -> {
            System.out.println("开始监控：" + target.getName());
            System.out.println("监控地址：" + target.getUrl());
            System.out.println("检查间隔：" + target.getCheckInterval() + "分钟");
            System.out.println("----------------------------------------");

            // 初始化文章集合
            Set<Article> previousArticles = Collections.newSetFromMap(new ConcurrentHashMap<>());
            previousArticlesMap.put(target, previousArticles);

            // 首次运行，获取当前文章列表
            List<Article> currentArticles = fetchArticles(target);
            if (currentArticles != null) {
                previousArticles.addAll(currentArticles);
                System.out.println(target.getName() + " 初始化完成，当前共有 " + previousArticles.size() + " 篇文章");
                System.out.println("----------------------------------------");
            }

            // 定时检查新文章
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(target.getCheckInterval() * 60 * 1000L);
                    System.out.println(target.getName() + " 执行检查 - " + LocalDateTime.now().format(formatter));
                    checkForNewArticles(target);
                    System.out.println("----------------------------------------");
                } catch (InterruptedException e) {
                    System.out.println(target.getName() + " 监控被中断：" + e.getMessage());
                    break;
                }
            }
        });
    }

    private static void checkForNewArticles(MonitorTarget target) {
        List<Article> currentArticles = fetchArticles(target);
        if (currentArticles == null) return;

        Set<Article> previousArticles = previousArticlesMap.get(target);
        for (Article article : currentArticles) {
            if (!previousArticles.contains(article)) {
                System.out.println("【" + target.getName() + " - 发现新文章】");
                System.out.println("发布时间：" + article.getDate());
                System.out.println("文章标题：" + article.getTitle());
                previousArticles.add(article);
            }
        }
    }

    private static List<Article> fetchArticles(MonitorTarget target) {
        try {
            List<Article> articles = new ArrayList<>();
            Document doc = Jsoup.connect(target.getUrl())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();

            Elements articleElements = doc.select(target.getArticleSelector());
            for (Element element : articleElements) {
                String title = element.select(target.getTitleSelector()).text().trim();
                String date = element.select(target.getDateSelector()).text().trim();
                if (!title.isEmpty() && !date.isEmpty()) {
                    articles.add(new Article(title, date));
                }
            }
            return articles;
        } catch (IOException e) {
            System.out.println(target.getName() + " 获取页面内容失败：" + e.getMessage());
            return null;
        }
    }
} 