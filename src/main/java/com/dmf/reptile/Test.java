package com.dmf.reptile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpHost;
import org.jsoup.nodes.Document;

import com.dmf.reptile.utils.HttpClientResult;
import com.dmf.reptile.utils.HttpClientUtil;
import com.dmf.reptile.utils.JsoupUtil;

public class Test {

	public static List<Map<String, String>> proxydata = new ArrayList<>();
	public static String[] links = { 
			"https://blog.csdn.net/qq_34609889/article/details/88692358",
			"https://blog.csdn.net/qq_34609889/article/details/88733153",
			"https://blog.csdn.net/qq_34609889/article/details/88692340",
			"https://blog.csdn.net/qq_34609889/article/details/86714796",
			"https://blog.csdn.net/qq_34609889/article/details/86679463",
			"https://blog.csdn.net/qq_34609889/article/details/86649114",
			"https://blog.csdn.net/qq_34609889/article/details/86624259",
			"https://blog.csdn.net/qq_34609889/article/details/86601725",
			"https://blog.csdn.net/qq_34609889/article/details/86584332",
			"https://blog.csdn.net/qq_34609889/article/details/86572821",
			"https://blog.csdn.net/qq_34609889/article/details/86565783",
			"https://blog.csdn.net/qq_34609889/article/details/86565398",
			"https://blog.csdn.net/qq_34609889/article/details/85722284",
			"https://blog.csdn.net/qq_34609889/article/details/85684626",
			"https://blog.csdn.net/qq_34609889/article/details/85367876",
			"https://blog.csdn.net/qq_34609889/article/details/85338253",
			"https://blog.csdn.net/qq_34609889/article/details/85317582",
			"https://blog.csdn.net/qq_34609889/article/details/85309688",
			"https://blog.csdn.net/qq_34609889/article/details/88667374"};
	public static int num = -200;
	
	public static String ip = "{\"code\":0,\"success\":true,\"msg\":\"0\",\"data\":[{\"ip\":\"125.33.66.176\",\"port\":4273},{\"ip\":\"125.33.50.92\",\"port\":4273},{\"ip\":\"124.64.245.25\",\"port\":4273},{\"ip\":\"114.240.97.249\",\"port\":4273},{\"ip\":\"114.240.96.212\",\"port\":4273},{\"ip\":\"124.64.243.153\",\"port\":4273},{\"ip\":\"123.122.152.49\",\"port\":4273},{\"ip\":\"123.122.156.58\",\"port\":4273},{\"ip\":\"123.119.47.6\",\"port\":4273},{\"ip\":\"125.33.62.35\",\"port\":4273},{\"ip\":\"111.196.134.66\",\"port\":4273},{\"ip\":\"123.122.154.25\",\"port\":4273},{\"ip\":\"114.250.178.194\",\"port\":4273},{\"ip\":\"125.33.71.63\",\"port\":4273},{\"ip\":\"125.33.59.157\",\"port\":4273},{\"ip\":\"123.119.42.1\",\"port\":4273},{\"ip\":\"123.122.154.99\",\"port\":4273},{\"ip\":\"124.64.242.105\",\"port\":4273},{\"ip\":\"125.33.58.95\",\"port\":4273},{\"ip\":\"125.33.52.234\",\"port\":4273}]}";

	public static void main(String[] args) throws Exception {

		ExecutorService pool = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 3; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println("线程"+Thread.currentThread().getName()+"启动！");
					test();
					
					
				}
			});
		}
		pool.shutdown();
		//test();
		//getAllUseProxy();
		
	}

	public synchronized static void addNum() {
		num+=200;
		try {
			//线程间隔三秒启动，快代理设置了同一时间同一个ip只能访问一次，短时间内次数太多容易被封ip;
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void test() {
		// 第一层循环取出每页的代理ip
		addNum();
		for (int i = num; i < num+200; i++) {
			//快代理高匿名代理的url规则
			String url = "https://www.kuaidaili.com/free/inha/" + (i + 1) + "/";
			//普通代理url规则
			// String url = "https://www.kuaidaili.com/free/intr/" + (i + 1) + "/";
			
			//获取代理服务器信息
			proxydata = getProxy(url);

			// 循环通过代理IP访问link
			for (Map<String,String> map : proxydata) {
				// 生成代理服务器对象
				HttpHost proxy = new HttpHost(map.get("ip").toString(), Integer.parseInt(map.get("port").toString()),
						map.get("type").toString());
				int code = 0;
				// 每个link访问一遍
				for (String link : links) {
					try {
						code = doget(link,proxy).getCode();
						// 如果不是200，说明该ip不能访问csdn，直接跳过后续的link，使用下一个代理服务器
						if (code!=200) {
							break;
						}
						System.out.println("第"+(i+1)+"页代理地址：" + map.get("ip") + ":" + map.get("port") + "------访问结果：" + code);
					} catch (Exception e1) {
						System.out.println("第"+(i+1)+"页代理ip无效！"+ map.get("ip") + ":" + map.get("port"));
						// 直接退出循环，无需每个link都去访问
						break;
					}
				}
			}
		}
	}
	/**
	 * 获取所有能用的代理谢谢，保存到文件中
	 * @return
	 */
	public static void getAllUseProxy(){
		for (int i = 0; i < 2768; i++) {
			//快代理高匿名代理的url规则
			String url = "https://www.kuaidaili.com/free/inha/" + (i + 1) + "/";
			
			List<Map<String, String>> data = new ArrayList<>();
			//获取代理服务器信息
			data = getProxy(url);
			String path = "proxydata.txt";
			
			FileWriter file;
			BufferedWriter write=null;
			try {
				file = new FileWriter(path);
				write= new BufferedWriter(file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			for (Map<String, String> map : data) {
				// 生成代理服务器对象
				HttpHost proxy = new HttpHost(map.get("ip").toString(), Integer.parseInt(map.get("port").toString()),
						map.get("type").toString());
				int code = 0;
				
				try {
					code = doget(links[0],proxy).getCode();
					if (code==200) {
						try {
							String proxyStr = map.get("ip")+":"+map.get("port")+":"+map.get("type")+"\n";
							write.write(proxyStr);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally {
							write.close();
						}
					}else {
						//访问不成功，直接执行下一个循环
						continue;
					}
				} catch (Exception e) {
					System.out.println("第"+(i+1)+"页代理ip无效！"+ map.get("ip") + ":" + map.get("port"));
					// 直接退出循环，无需每个link都去访问
				}
			}
		}
	}

	public static  List<Map<String, String>> getProxyToFile(){
		List<Map<String, String>> list = new ArrayList<>();
		BufferedReader reader = null;
		try {
			FileReader file = new FileReader("proxydata.txt");
			reader = new BufferedReader(file);
			String str;
			while((str = reader.readLine())!=null) {
				String strs[] = str.split(":");
				Map<String,String> map = new HashMap<>();
				if(strs.length>0) {
					map.put("ip", strs[0]);
					map.put("port", strs[1]);
					map.put("type", strs[2]);
					list.add(map);
				}
			}
		} catch (IOException e) {
			
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * @Descript:获取代理服务器信息（ip、端口、类型）
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, String>> getProxy(String url){

		//如果代理服务器网站做了封ip的策略，如果访问过于频繁可能会被封ip，用单线程的话应该不会，但是使用多线程的话很容易被封，我用10个线程跑过，瞬间被封了。
		//可以通过设置代理服务器去访问，去其他代理服务器网站找个免费能用的就行。
		//HttpHost proxy = new HttpHost("163.125.232.238",8118);
		
		Document doc = null;
		//请求结果
		HttpClientResult result = null;
		try {
			//
			result = doget(url,null);
			//result = doget(url,proxy);
			
			//如果返回不是200，说明没有获取到代理服务器信息
			if(result.getCode()!=200)
				throw new Exception();
			//通过httpclient取到html网页
			doc = JsoupUtil.getDocByHtml(result.getContent());
		} catch (Exception e) {
			System.out.println("获取代理服务器信息失败！");
		}
		
		//使用jsoup自带的访问url
		// Document doc = JsoupUtil.getDocByUrl(url);
		List<Map<String, String>> list = JsoupUtil.getData(doc);
		return list;
	}

	//使用post请求
	public static HttpClientResult dopost(String url, HttpHost proxy) throws Exception {
		//设置请求头集合
		Map<String, String> headers = new HashMap<String, String>();
		// headers.put("Cookie", "123");
		headers.put("Connection", "keep-alive");
		headers.put("Accept", "application/json");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9");
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		HttpClientResult result = HttpClientUtil.doPost(url, headers, null,proxy);
		return result;
	}

	//使用get请求
	public static HttpClientResult doget(String url,HttpHost proxy) throws Exception {
		//设置请求头集合
		Map<String, String> headers = new HashMap<String, String>();
		// headers.put("Cookie", "123");
		//headers.put("Connection", "keep-alive");
		headers.put("Accept", "application/json");
		headers.put("cache-control", "max-age=0");
		headers.put("authority", "blog.csdn.net");
		headers.put("accept-encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9");
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
		HttpClientResult result = HttpClientUtil.doGet(url, headers, null,proxy);
		return result;
	}
}
