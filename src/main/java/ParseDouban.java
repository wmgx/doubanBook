import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParseDouban {
    static Map<String, String> parseDocument(Document document) {
        Map<String, String> infos = new HashMap<String, String>();
        // 书名
        infos.put("书名", document.getElementsByTag("h1").text());
        // 封皮链接
        infos.put("封皮链接", document.getElementById("mainpic").getElementsByTag("img").attr("src"));
        String autho =document.getElementById("info").select("span").first().select("a").text();
        if(StringUtil.isBlank(autho)) autho=document.getElementById("info").select("a").first().text();
        infos.put("作者", autho.trim());

        //出版社: 出品方: 副标题: 译者: 出版年: 页数: 定价: 装帧: ISBN: 如果其中有空格可能会出错，暂时没有想到解决办法
        String[] infoKeys = {"出版社", "出品方", "副标题", "译者", "出版年", "页数", "定价", "装帧", "ISBN"};
        String infoText = document.getElementById("info").text();
        for (String infoKey : infoKeys) {
            if (infoText.contains(infoKey)) {
                // 比如出版社名字里面也有出版社这三个字符，直接用split会被当成分隔符，所以加一个：区分一下
                String s = infoText.split(infoKey + ":")[1].split(" ")[1];
                infos.put(infoKey, s.trim());
            } else {
                infos.put(infoKey, "无");
            }
        }
        // 内容简介
        StringBuilder contentDecText = new StringBuilder();
        try {
            Elements contentDec = document.getElementById("link-report").getElementsByTag("p");
            for (Element element : contentDec) {
                contentDecText.append(element.text()).append("\n");
            }
            infos.put("内容简介", contentDecText.toString().trim());
        } catch (Exception e) {
            infos.put("内容简介", "无");
        }
        // 作者简介
        StringBuilder authorProfileText = new StringBuilder();
        try {
            Elements authorProfile = document.select("div.intro").eq(1).get(0).getElementsByTag("p");
            for (Element element : authorProfile) {
                authorProfileText.append(element.text()).append("\n");
            }
            infos.put("作者简介", authorProfileText.toString().trim());
        } catch (Exception e) {
            infos.put("作者简介", "无");
        }
        // 目录
        try {
            String s = document.select("div[id^=dir]").select("div[id$=full]").html();
            infos.put("目录", document
                    .select("div[id^=dir]")
                    .select("div[id$=full]")
                    .html(s.replace("<br>", "$$$$$$$$$$"))
                    .text()
                    .replace("$$$$$$$$$$", "\n").replace("· · · · · · (收起)", "").trim());
        } catch (Exception e) {
            infos.put("目录", "无");
        }
        // 标签
        StringBuilder tagText = new StringBuilder();
        try {
            Elements tags = document.select("a.tag");
            for (Element tag : tags) {
                tagText.append(tag.text()).append(",");
            }
            infos.put("标签", tagText.toString());
            return infos;
        } catch (Exception e) {
            infos.put("标签", "无");
        }
        return  infos;
    }

    static Map<String, String> getInfo(String url, String cookie) throws IOException {
        return parseDocument(Jsoup.connect(url)
                .header("Cookie", cookie).get());
    }

    static Map<String, String> getInfo(String url) throws IOException {
        return parseDocument(Jsoup.connect(url).get());
    }
}
