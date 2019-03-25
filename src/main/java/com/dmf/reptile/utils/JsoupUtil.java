package com.dmf.reptile.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author: dmf
 * @date: 2019年3月19日
 * @Description:jsoup工具类,主要用来爬取免费代理服务ip
 */
public class JsoupUtil {

	/**
	 * @Description: 通过url获取document对象
	 * @param url:请求地址
	 * @return
	 * @throws IOException
	 */
	public static Document getDocByUrl(String url) throws IOException{
		Document doc = Jsoup.connect(url)
				  .userAgent("Mozilla")
				  .cookie("auth", "token")
				  .timeout(3000)
				  .post();
		return doc;
	}
	
	/**
	 * @Description:通过一个html字符串获取document对象
	 * @param html:网页的html代码字符串
	 * @return
	 */
	public static Document getDocByHtml(String html){
		Document doc = Jsoup.parse(html);
		return doc;
	}
	/**
	 * @Description:解析网页，获取网页上的免费代理ip和端口信息。根据具体网页不同，解析也过程不同，所以需要根据具体网页编写该方法。
	  *                                      此方法解析的是快代理的免费代理。（https://www.kuaidaili.com/free）
	 * @param doc:网页的doc对象
	 * @return
	 */
	public static List<Map<String, String>> getData(Document doc) {
		List<Map<String, String>> list = new ArrayList<>();
		Element ele = doc.getElementById("list");
		Elements eletrs = ele.getElementsByTag("tr");
	    
		//循环tr标签
		for (Element eletr : eletrs) {
			Elements eletds = eletr.getElementsByTag("td");
			//保存td标签里的ip和port的值
			Map<String, String> map = new HashMap<>();
			for (Element eletd : eletds) {
				
				if("IP".equals(eletd.attr("data-title"))){
					map.put("ip", eletd.text());
					//System.out.println(element.text());
				}
				if("PORT".equals(eletd.attr("data-title"))) {
					map.put("port", eletd.text());
				}
				//获取代理服务器类型。http/https
				if("类型".equals(eletd.attr("data-title"))) {
					map.put("type", eletd.text());
				}
			}
			if(!map.isEmpty()) {
				list.add(map);
			}
		}
		return list;
	}
}
