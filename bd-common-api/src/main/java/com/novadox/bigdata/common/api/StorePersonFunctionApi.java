package com.novadox.bigdata.common.api;


import com.novadox.bigdata.common.model.Constants;
import com.novadox.bigdata.common.model.Person;
import org.springframework.data.gemfire.function.annotation.OnRegion;

@OnRegion(region = Constants.PERSON_REGION)
public interface StorePersonFunctionApi {

    public void storePersonArray(Person[] personArray);
    public void storeSinglePerson(Person person);
}
