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
    	//�õ����������֣�ȡ��name�У�
    	name = page.getHtml().xpath("h3/text()").toString();
    	page.putField("name", name);
    	//�г��ۣ�ȡ��market�У�����ȥ����һ������ҷ���
    	market = page.getHtml().css("div.mod-hotEquipment-bd").xpath("b/text()").toString().substring(1);
    	double Dmarket = Double.parseDouble(market);
    	page.putField("market", market);
    	
        //���ۼۣ�ȡ��sell�У�
        sell = page.getHtml().css("div.mod-hotEquipment-bd").xpath("strong/text()").toString().substring(1);
        double Dsell = Double.parseDouble(sell);
        page.putField("sell", sell);
        
        
        Dratio = Dsell/Dmarket;
        System.out.println(Dratio+" "+sell+"  "+market+"  "+name);
        if(Dratio < 0.67){
        	final Properties props = new Properties();
            /*
             * ���õ����ԣ� mail.store.protocol / mail.transport.protocol / mail.host /
             * mail.user / mail.from
             */
            // ��ʾSMTP�����ʼ�����Ҫ���������֤
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.163.com");
            // �����˵��˺�
            props.put("mail.user", "j172045149@163.com");
            // ����SMTP����ʱ��Ҫ�ṩ������
            props.put("mail.password", "j263686661");

            // ������Ȩ��Ϣ�����ڽ���SMTP���������֤
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // �û���������
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // ʹ�û������Ժ���Ȩ��Ϣ�������ʼ��Ự
            Session mailSession = Session.getInstance(props, authenticator);
            // �����ʼ���Ϣ
            MimeMessage message = new MimeMessage(mailSession);
            // ���÷�����
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

            // �����ռ���
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

            // �����ʼ�����
            try {
				message.setSubject("�л���������");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // �����ʼ���������
            try {
				message.setContent(Dratio+" "+sell+"  "+market+"  "+name, "text/html;charset=UTF-8");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // �����ʼ�
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
                //��"https://github.com/code4craft"��ʼץ
                .addUrl("http://www.igxe.cn/category-1")
                //����5���߳�ץȡ
                //.addPipeline(new JsonFilePipeline("G:\\"))
                .thread(5)
                //��������
                .run();
        
    }
       
    }
}
