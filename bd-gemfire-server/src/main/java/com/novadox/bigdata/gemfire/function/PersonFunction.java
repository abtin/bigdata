package com.novadox.bigdata.gemfire.function;


import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.novadox.bigdata.common.model.Constants;
import com.novadox.bigdata.common.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class PersonFunction {
    private static Logger log = LoggerFactory.getLogger(PersonFunction.class);

    @Resource(name= Constants.PERSON_REGION)
    private Region<String, Person> personRegion;

    @GemfireFunction(hasResult = true)
    public void storePersonArray(FunctionContext functionContext, Person[] personArray) {
        long start = System.currentTimeMillis();
        if (personArray == null || personArray.length ==0) {
            log.warn("storePersonArray() is called with and empty array of Person. Why?");
            return;
        }

        Assert.isInstanceOf(RegionFunctionContext.class, functionContext,
                "Expecting a RegionFunctionContext. Make sure the functions runs on Region");

        RegionFunctionContext rfc = ((RegionFunctionContext) functionContext);

        Map<String, Person> chunk = new HashMap<String, Person>(personArray.length);
        for (Person person : personArray) {
            chunk.put(person.getKey(), person);
        }
        personRegion.putAll(chunk);

        long totalTime = System.currentTimeMillis() - start;
        log.info("PersonFunction.storePersonArray() finished in [{}] milliseconds.", totalTime );

        rfc.getResultSender().lastResult("storePersonArray executed successfully!");
    }

    @GemfireFunction(hasResult = true)
    public void storeSinglePerson(FunctionContext functionContext, Person person) {
        long start = System.currentTimeMillis();
        if (person == null) {
            log.warn("storeSinglePerson() is called with 'null' Person. Why?");
            return;
        }

        Assert.isInstanceOf(RegionFunctionContext.class, functionContext,
                "Expecting a RegionFunctionContext. Make sure the functions runs on Region");

        RegionFunctionContext rfc = ((RegionFunctionContext) functionContext);

        personRegion.put(person.getKey(), person);

        long totalTime = System.currentTimeMillis() - start;
        log.info("PersonFunction.storeSinglePerson() finished in [{}] milliseconds.", totalTime );

        rfc.getResultSender().lastResult("storeSinglePerson executed successfully!");
    }

}
