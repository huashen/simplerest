package com.ln.simplerest.support;

import java.lang.reflect.Method;

import com.ln.simplerest.support.guice.InjectorSingleton;
import com.ln.simplerest.util.ReflectionUtils;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * GuiceAdaptableJobFactory
 *
 * @author longhuashen
 * @since 17/9/21
 */
public class GuiceAdaptableJobFactory implements JobFactory {


    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        return newJob(bundle);
    }

    public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {
        try {
            Object jobObject = createJobInstance(bundle);
            return adaptJob(jobObject);
        }
        catch (Exception ex) {
            throw new SchedulerException("Job instantiation failed", ex);
        }
    }

    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        // Reflectively adapting to differences between Quartz 1.x and Quartz 2.0...
        Method getJobDetail = bundle.getClass().getMethod("getJobDetail");
        Object jobDetail = ReflectionUtils.invokeMethod(getJobDetail, bundle);
        Method getJobClass = jobDetail.getClass().getMethod("getJobClass");
        Class jobClass = (Class) ReflectionUtils.invokeMethod(getJobClass, jobDetail);

        //调用父类的方法
        Object jobInstance = jobClass.newInstance();
        //进行注入,这属于Spring的技术,不清楚的可以查看Spring的API.
        InjectorSingleton.getInstance().getInjector().injectMembers(jobInstance);
        return jobInstance;
    }

    protected Job adaptJob(Object jobObject) throws Exception {
        if (jobObject instanceof Job) {
            return (Job) jobObject;
        }
        else {
            throw new IllegalArgumentException("Unable to execute job class [" + jobObject.getClass().getName() +
                    "]: only [org.quartz.Job] and [java.lang.Runnable] supported.");
        }
    }

}
