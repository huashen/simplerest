package com.ln.simplerest.support;

import com.ln.simplerest.dao.GoodsDao;
import com.ln.simplerest.model.Goods;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * MyJob
 *
 * @author longhuashen
 * @since 17/9/21
 */
public class MyJob implements Job {

    @Inject
    private GoodsDao goodsDao;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Goods> goods = goodsDao.listAllGoods();
System.out.println(">>>>>>>>>>>>>>>>>>goods:" + goods);
        System.out.println(new Date() + ": doing something...");
    }
}
