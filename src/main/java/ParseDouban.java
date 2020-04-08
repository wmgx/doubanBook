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
        // ����
        infos.put("����", document.getElementsByTag("h1").text());
        // ��Ƥ����
        infos.put("��Ƥ����", document.getElementById("mainpic").getElementsByTag("img").attr("src"));
        String autho =document.getElementById("info").select("span").first().select("a").text();
        if(StringUtil.isBlank(autho)) autho=document.getElementById("info").select("a").first().text();
        infos.put("����", autho.trim());

        //������: ��Ʒ��: ������: ����: ������: ҳ��: ����: װ֡: ISBN: ��������пո���ܻ������ʱû���뵽����취
        String[] infoKeys = {"������", "��Ʒ��", "������", "����", "������", "ҳ��", "����", "װ֡", "ISBN"};
        String infoText = document.getElementById("info").text();
        for (String infoKey : infoKeys) {
            if (infoText.contains(infoKey)) {
                // �����������������Ҳ�г������������ַ���ֱ����split�ᱻ���ɷָ��������Լ�һ��������һ��
                String s = infoText.split(infoKey + ":")[1].split(" ")[1];
                infos.put(infoKey, s.trim());
            } else {
                infos.put(infoKey, "��");
            }
        }
        // ���ݼ��
        StringBuilder contentDecText = new StringBuilder();
        try {
            Elements contentDec = document.getElementById("link-report").getElementsByTag("p");
            for (Element element : contentDec) {
                contentDecText.append(element.text()).append("\n");
            }
            infos.put("���ݼ��", contentDecText.toString().trim());
        } catch (Exception e) {
            infos.put("���ݼ��", "��");
        }
        // ���߼��
        StringBuilder authorProfileText = new StringBuilder();
        try {
            Elements authorProfile = document.select("div.intro").eq(1).get(0).getElementsByTag("p");
            for (Element element : authorProfile) {
                authorProfileText.append(element.text()).append("\n");
            }
            infos.put("���߼��", authorProfileText.toString().trim());
        } catch (Exception e) {
            infos.put("���߼��", "��");
        }
        // Ŀ¼
        try {
            String s = document.select("div[id^=dir]").select("div[id$=full]").html();
            infos.put("Ŀ¼", document
                    .select("div[id^=dir]")
                    .select("div[id$=full]")
                    .html(s.replace("<br>", "$$$$$$$$$$"))
                    .text()
                    .replace("$$$$$$$$$$", "\n").replace("�� �� �� �� �� �� (����)", "").trim());
        } catch (Exception e) {
            infos.put("Ŀ¼", "��");
        }
        // ��ǩ
        StringBuilder tagText = new StringBuilder();
        try {
            Elements tags = document.select("a.tag");
            for (Element tag : tags) {
                tagText.append(tag.text()).append(",");
            }
            infos.put("��ǩ", tagText.toString());
            return infos;
        } catch (Exception e) {
            infos.put("��ǩ", "��");
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
