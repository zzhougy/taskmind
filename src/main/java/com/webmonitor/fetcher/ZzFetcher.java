package com.webmonitor.fetcher;

import com.webmonitor.WebMonitorEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ZzFetcher implements ContentFetcher {
    private List<WebContent> lastWeb = new ArrayList<>();
    private boolean isFirstLoad = true;

    @Override
    public List<WebContent> fetch() throws Exception {
        if (isFirstLoad) {
            log.info("开始监控{}...", getWebMonitorEnum().getName());
        } else {
            log.info("正在检查{}更新...", getWebMonitorEnum().getName());
        }

        Document doc = Jsoup.connect(getWebMonitorEnum().getUrl())
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();

        Elements newsElements = doc.select(".list01 li");
        List<WebContent> currentWeb = new ArrayList<>();

        for (Element element : newsElements) {
            Element link = element.selectFirst("a");
            Element date = element.selectFirst("span");

            if (link != null) {
                String title = link.text();
                String url = link.attr("abs:href");
                String dateStr = date != null ? date.text() : "";

                WebContent webContent = WebContent.builder()
                        .id(url)
                        .title(title)
                        .description(title)
                        .link(url)
                        .source(getWebMonitorEnum().getName())
                        .dateStr(dateStr)
                        .category(getWebMonitorEnum().getName())
                        .build();

                currentWeb.add(webContent);
            }
        }

        List<WebContent> newWeb = new ArrayList<>();
        if (!isFirstLoad) {
            newWeb = findNewWebContent(currentWeb, lastWeb);
            log.info("{}检查完成，发现 {} 条新内容", getWebMonitorEnum().getName(), newWeb.size());
        } else {
            log.info("首次加载{}，获取到 {} 条政策，不通知", getWebMonitorEnum().getName(), currentWeb.size());
        }
        
        lastWeb = currentWeb;
        isFirstLoad = false;
        return newWeb;
    }



    @Override
    public WebMonitorEnum getWebMonitorEnum() {
        return WebMonitorEnum.Zz;
    }
} 