package webmagicTest;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

public class JobProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        page.putField("div",page.getHtml().css("div.mt h2").all());
        //正则表达式
        page.putField("div3", page.getHtml().css("div#news_div a").regex(".*江苏.*").all());
        //处理结果API
        page.putField("div4", page.getHtml().css("div#news_div a").regex(".*江苏.*").get());
        page.putField("div5", page.getHtml().css("div#news_div a").regex(".*江苏.*").toString());

        page.addTargetRequests(page.getHtml().css("div#news_div").links().regex(".*9$").all());
        page.putField("url",page.getHtml().css("div.mt h1").all());
//        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
//        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
//        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
    }
    private Site site = Site.me()
            .setCharset("utf8")    //设置编码
            .setTimeOut(10000)   //设置超时时间，单位是ms毫秒
            .setRetrySleepTime(3000)  //设置重试的间隔时间
            .setSleepTime(3)      //设置重试次数
            ;
    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new JobProcessor())
                .addUrl("https://www.jd.com/moreSubject.aspx")
                .thread(5)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)));
        Scheduler scheduler = spider.getScheduler();
        spider.run();

    }
}
