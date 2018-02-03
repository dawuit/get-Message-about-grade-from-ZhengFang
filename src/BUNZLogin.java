import java.net.*;
import java.util.Scanner;
import java.io.*;
/**
 * 2018-2-3 21:15:16
 * @Description: 模拟登陆正方教务获取成绩HTML页面，并利用正则表达式提取成绩信息
 * @version: 1.0
 * @author: dawuit
 */

class CallBNUZ
{
	/**登陆域名*/
	public static final String urlStr= "http://es.bnuz.edu.cn";
	/**默认登陆页面*/
	public static final String loginPath = "/default2.aspx";
	/**成绩页面*/
	public static final String gradePath = "/jwgl/xscjcx.aspx";
	/**学号*/
	private String userName;
	/**密码*/
	private String password;
	/**cookie*/
	private String cookie;
	public CallBNUZ() throws IOException
	{
	}
	/**
	 * 
	 * @param userName
	 * @param password
	 * @return String 
	 * 如果密码错误，返回null；如果登陆成功返回登陆跳转后页面的HTML
	 * @throws IOException
	 */
	public String doLogin(String userName, String password) throws IOException
	{
		this.userName = userName;
		this.password = password;
		//创建登陆链接
		HttpURLConnection bnuzCon = (HttpURLConnection)new URL(urlStr + loginPath).openConnection();			;
		//设置参数
		bnuzCon.setDoOutput(true);
		bnuzCon.setRequestMethod("POST");	
		//禁止304状态码自动跳转
		bnuzCon.setInstanceFollowRedirects(false);
		//打开输入流，写入POST信息
		OutputStreamWriter ow =  new OutputStreamWriter(bnuzCon.getOutputStream(), "UTF-8");
		ow.write("__EVENTTARGET=&__EVENTARGUMENT=&__LASTFOCUS=&__VIEWSTATE=%2FwEPDwUKMTk5OTI2NTE2OQ8WAh4IdXNlcm5hbWVoFgICAQ9kFgICAQ8PFgIeBFRleHQFCjE2MDEwMTAwOTBkZGTaoOQtC3whjfNTIbaHhWwLNFGiLA%3D%3D&__VIEWSTATEGENERATOR=09394A33&__PREVIOUSPAGE=P41Qx-bOUYMcrSUDsalSZQ66PXL-H_8xeQ4t7bJ3gWnYCDI-j8Z8SOoK8eM1&__EVENTVALIDATION=%2FwEWCwLzspvTCgLs0bLrBgLs0fbZDAK%2FwuqQDgKAqenNDQLN7c0VAveMotMNAu6ImbYPArursYYIApXa%2FeQDAoixx8kBoxj23yQZO5mYMsM7hNGpjSVsk%2Bs%3D&TextBox1=" + userName + "&TextBox2="+ password + "&RadioButtonList1=%E5%AD%A6%E7%94%9F&Button4_test=");
		ow.flush();
		//向服务器发起连接请求
		bnuzCon.connect();
		//获取响应头cookie字段（保存sessionId），保持登录状态。
		cookie = (cookie = bnuzCon.getHeaderField("Set-Cookie")).substring(0, cookie.indexOf(';'));
		//打开输入流；读取返回的HTML页面
		BufferedReader br = new BufferedReader(new InputStreamReader(bnuzCon.getInputStream(), "UTF-8"));
		StringBuffer strTemp = new StringBuffer();
		String strTempLine;
		while((strTempLine = br.readLine()) != null)
		{
			strTemp.append(strTempLine).append("\n");
		}
		//返回HTML页面中有“密码不正确”字样则密码错误
		return strTemp.indexOf("密码不正确") != -1 ? null : strTemp.toString();
	}
	/**
	 * 
	 * @return 
	 * 返回成绩信息；
	 * 一维为科目，二维为每一科的信息，如课程名、绩点、分数...
	 * String[0][]存储各字段名，下标1开始为各课程信息
	 */
	public String[][] getGrade()
	{
		String HTML = new String();
		
		try 
		{
			//获取成绩页面HTML
			HTML = getGradeHTML();
			//对HTML页面正则匹配，去除标签、空白符
			HTML = HTML.substring(HTML.indexOf("<table"), HTML.lastIndexOf("</table>"));
			HTML = HTML.replaceAll("<.*?>|&nbsp;", " ").trim();
			//label为字段信息（名称、绩点、课程性质、分数...）
			String[] label = HTML.substring(0, HTML.indexOf("\n")).split("\\s+");
			String[] gradeTemp = HTML.substring(HTML.indexOf('\n') + 1).split("\n\\s*\n");
			//各科信息
			String[][] gradeMess = new String[gradeTemp.length + 1][gradeTemp[0].split("\\s+").length];
			//下标0存放字段名信息；整合成二维数组返回
			gradeMess[0] = label;
			for(int i = 0; i < gradeTemp.length; ++i)
			{
				gradeMess[i+1] = gradeTemp[i].trim().split("\\s+");
			}
			//返回成绩信息
			return gradeMess;
		} 
		catch (IOException e) 
		{
			// TODO 自动生成的 catch 块
			System.out.println("获取成绩页面失败");
		}
		
		return null;
	}
	/**
	 * 
	 * @return String
	 * 返回成绩页面的HTML
	 * @throws IOException
	 */
	private String getGradeHTML() throws IOException
	{
		//创建成绩页面链接
		HttpURLConnection gradeCon = (HttpURLConnection)new URL(urlStr + gradePath).openConnection();
		//设置参数
		gradeCon.setDoOutput(true);
		gradeCon.setUseCaches(false);
		gradeCon.setRequestMethod("POST");
		gradeCon.setRequestProperty("Cookie", cookie);
		gradeCon.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0");
		//写入POST信息
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(gradeCon.getOutputStream(), "UTF-8"));
		bw.write("ScriptManager1=ScriptManager1%7CButton6&ScriptManager1_HiddenField=%3B%3BAjaxControlToolkit%2C%20Version%3D1.0.20229.20821%2C%20Culture%3Dneutral%2C%20PublicKeyToken%3D28f01b0e84b6d53e%3Azh-CN%3Ac5c982cc-4942-4683-9b48-c2c58277700f%3Ae2e86ef9%3A1df13a87%3Aaf22e781%3B%3BAjaxControlToolkit%2C%20Version%3D1.0.20229.20821%2C%20Culture%3Dneutral%2C%20PublicKeyToken%3D28f01b0e84b6d53e%3Azh-CN%3Ac5c982cc-4942-4683-9b48-c2c58277700f%3Ae2e86ef9%3A1df13a87%3Aaf22e781&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=x73Hc5Mx%2FyRdwaGpmIJAqkbBtzP0MMBQusbqKfRS6UewGfPauOKSLU22JAjX3evozrl7ZVTN8LWeP1jZo8ZPdJmMxm8MSD2GTEpjrCABfQsqMOTxxWMtpGGak%2FsL8PEIywo87OLrZqUzIDUpGfq794MZI9LtIHZOOWAM942H%2BhWFMp9O146DleoboZADuJwC2BrZjq78xHqSo8cAkWxmNJemh2hiMY%2BjUGTnl0Nh%2B0n%2BB%2FvH1lkYtZ8fzfWQ8KXxX53raqdmo51Z2pVs2nVRPh9mDALSAIlI1kFJCw4FWE89H5Qs3I9Od%2FcrwpLmKdACJifly55IhSTX2AhLJQ1YJhEt%2BlvN6ucLyNKf107mg4g%2Be5TD2WJBctSAWuAEVxQfldCdXfyGOLvqdnR%2BSmCQzVdgH9GcS3q6g0%2BCp2M1O9Ex7Aa%2BtPEvo6mkKWvCoFvaYSRPBZAz%2BOtKsJOx%2Bwz9H1J%2F1K9aWUvFFfdbCq3VHfuK0JQpmFSVK%2FdI1UE5rGZqsH9xLsBz4S6LZHLJtHpOPzPRAhhR8%2BcRsSHrze0LHmnzwglHC4SAbYRiluPnheS%2BrsNjfbcQSkiKWByJqV7ZcuTuoqSPVlKz3TRMRvgbYO23414iPse%2FyzJ06jHTshpEbG9AMpRJT%2Fy3qEch%2F7cDO9cwDTOSVXo88L0dbDrOFQL7AnnbWi%2F4Io50BjrsV7UxvVTP6ZX7j2e27U9j5%2B8cNCQ6ael1BVnIpYQW%2FnNUFDCb%2BQDEBEWn6IpOITVs5LXx5kcbifFID%2FUryJuHa%2BinYZaDogaOv9ufsgEhWDP%2BuU5RV2hljT6xWym1C7oaMzfXczvJ7ao92T9dezvdllOr1Uf1kBg6C2rQy%2BFKT62amQDjQwaOP12t20Yiwt3IL6oHbF71HemQeUo1I1f0d%2FplCbxKZ%2B6wCEK%2FG3FKjcNos%2Froz%2BiDzwyzG%2By5j2xF8%2F2rplTZQ2od%2Fj0DxM2arHPaJg2uNqMNWx79hflqq7YPRji43k50GIdy%2BGPzwgjK9g5ahlRx6nnlHY0J5bSDwkrxbXuiBkIaa7Nzfb1tdiZ9svkShHA2d2zpjXY%2FgwtWbhj3PCop8A2qq0QjvJBOHSDbHKomZs4lepOib6gBbHDSX4hKT7%2Bl8VKB7751wBF3zCAK2EcQmf%2FVujzCjlNVPXGv1pj%2BZbJbGhC5sMDwu3d2JYHvsTErH83cgY3zy8ADALaAes8P7PCxXPsx1HYlKpNv5qgOYTo6fxdPoM42Sr6iNo3Urxz2DXk6A7FBagryKgpiEaaeIhh13bFsoBgr6AJctSyhztUjkvNRXsltwJuAXsCg6q92D4P3uZcH3AXpDFo3Jn3oZuQEq3L%2BLGRIP3MseWXrWTnp5FBfMrdgjniRXJXAh4wIBHZ1gVfz4W65eiN92DG52rRE35QbE%2BU5AZkXRtGZ7%2BfjBnpu4Jj8wpRtPjQyZu%2BrzigxJq2pO7AbMS3o6NG%2Fz05gAOfYj0eW2bwzYTla43e7C3c7%2B%2BBMIRO55I%2BI05Kedr0D86%2FnxgPHXKYzjULN6ZA9qhxAf5qwLIFQVcz776BJcOCSLCwVRaLsbD9n4jo1okoimnl%2FVne9SDyEFuLZ8DKLVCiV3GkTjeqzpxH2LCz440WemecIfBTU4aLI6gnKUY%2FSoJDLSvNMvD1FMopvFEkJYp64rfOFmowBkT9rKus6h%2FFVOkwzAgyB1lL7LLni1EE%2BniiEJmOnMWtz5Ym4YEI3PbhZKAnDvHqQAZjPjEfv5W2mfCZj4L%2FSARrK2qWFTKh4BliXLS0Pf%2B%2BlF5r2bK2O2TUf6G5NtOT1R7TtPTD9VT5J7DqJ2chVBTOO8YzV30%2FcGKn0Q2PWtmSN8VUlW0cQSLn8ElNk%2FHs6Tg3bPlGRUZb34%2BfQhhqCxMo8S3TX2TqeBYCGa3z5BFOpQYKDsGrnyOeA89Y5SIsRyhfVjaDa%2BD9j2RTxFtsSAdsAa3mrtY4Us%2BVg9qlWUnCycOUzcwl%2BUhhqL1OhXCxKbsaKktWbGAJWtO1%2B8Pt%2FPOmUKsjCgJ3bnu5%2FZCXtyCdX4%2BD8ZONDyZTwhsG%2FSUU9I3WZLKbeS0%2BPRswnpesE7ntIDMTfJQh6GOxX74lM%2BFrhcUuh%2BFn3h%2BmaYaVTA7%2Bi7NpbSJBs28z5xKuJ2LSg%2B7TvmKqGqag8WDzL4lNukQMT1JClfqnfzQ19X5XYikbzHIazfS1X8wiQF%2FmfqOpSJ7LbzPgg0qpnoJgDQhD9G0JaLQ5v0h9FY4lJbTiXNMoBxU8NzLqVCc5ExkU5J28R0dX8GQu%2Fsm0uXvDI%2FLtzWuw%2Fk50KyZCMH7Xz8tZJy%2FO8vFU1E3s%3D&__VIEWSTATEGENERATOR=0FF955E6&__VIEWSTATEENCRYPTED=&ccd_xn_ClientState=2017-2018%3A%3A%3A2017-2018&ccd_xq_ClientState=1%3A%3A%3A1&ddlXN=2017-2018&ddlXQ=1&hiddenInputToUpdateATBuffer_CommonToolkitScripts=1&__ASYNCPOST=true&Button6=%E4%B8%BB%E4%BF%AE%E4%B8%93%E4%B8%9A%E6%9C%80%E9%AB%98%E6%88%90%E7%BB%A9%E6%9F%A5%E8%AF%A2");
		bw.flush();
		gradeCon.connect();
		//读取返回HTML信息
		BufferedReader br = new BufferedReader(new InputStreamReader(gradeCon.getInputStream(), "UTF-8"));
		StringBuffer strTemp = new StringBuffer();
		String strTempLine;
		while((strTempLine = br.readLine()) != null)
		{
			strTemp.append(strTempLine).append("\n");
		}

		return strTemp.toString();
	}
}

public class BUNZLogin 
{
	public static void main(String[] args) throws IOException
	{
		String userName, password;
		Scanner scan = new Scanner(System.in);
		System.out.print("请输入学号:");
		userName = scan.next();
		System.out.print("请输入密码:");
		password = scan.next();
		CallBNUZ myLogin = new CallBNUZ();
		String res = myLogin.doLogin(userName, password);
		if(res == null)
		{
			System.out.println("密码错误");
			return;
		}
		//打印信息
		String[][] gradeMess = myLogin.getGrade();
		for(String[] i : gradeMess)
		{
			for(String j : i)
			{
				System.out.printf("%-30s", j);
			}
			System.out.println();
		}
	}
	
}
