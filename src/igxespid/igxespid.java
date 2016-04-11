package igxespid;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;





public class igxespid implements PageProcessor {
	public static String name;
	public static String market;
	public static String sell;
	public static double Dratio;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
   
    public void process(Page page) {
    	//得到武器的名字，取到name中；
    	name = page.getHtml().xpath("h3/text()").toString();
    	page.putField("name", name);
    	//市场价，取到market中；并且去掉第一个人民币符号
    	market = page.getHtml().css("div.mod-hotEquipment-bd").xpath("b/text()").toString().substring(1);
    	double Dmarket = Double.parseDouble(market);
    	page.putField("market", market);
    	
        //销售价，取到sell中；
        sell = page.getHtml().css("div.mod-hotEquipment-bd").xpath("strong/text()").toString().substring(1);
        double Dsell = Double.parseDouble(sell);
        page.putField("sell", sell);
        
        
        Dratio = Dsell/Dmarket;
        System.out.println(Dratio+" "+sell+"  "+market+"  "+name);
        if(Dratio < 0.67){
        	final Properties props = new Properties();
            /*
             * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
             * mail.user / mail.from
             */
            // 表示SMTP发送邮件，需要进行身份验证
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.163.com");
            // 发件人的账号
            props.put("mail.user", "j172045149@163.com");
            // 访问SMTP服务时需要提供的密码
            props.put("mail.password", "j263686661");

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            InternetAddress form = null;
			try {
				form = new InternetAddress(
				        props.getProperty("mail.user"));
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				message.setFrom(form);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // 设置收件人
            InternetAddress to = null;
			try {
				to = new InternetAddress("2673245113@qq.com");
			} catch (AddressException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            try {
				message.setRecipient(RecipientType.TO, to);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // 设置邮件标题
            try {
				message.setSubject("有货啦！！！");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // 设置邮件的内容体
            try {
				message.setContent(Dratio+" "+sell+"  "+market+"  "+name, "text/html;charset=UTF-8");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // 发送邮件
            try {
				Transport.send(message);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
       if (page.getResultItems().get("name") == null) {
           
          page.setSkip(true);
        }
       page.addTargetRequests(page.getHtml().links().regex("(http://www.igxe.cn/category-\\w+)").all()); 
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws InterruptedException,MessagingException {
    	while(true){
        Spider.create(new igxespid())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.igxe.cn/category-1")
                //开启5个线程抓取
                //.addPipeline(new JsonFilePipeline("G:\\"))
                .thread(5)
                //启动爬虫
                .run();
        
    }
       
    }
}
