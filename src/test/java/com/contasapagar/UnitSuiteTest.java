package com.contasapagar;

import com.contasapagar.api.mapper.ContaMapperTest;
import com.contasapagar.domain.conta.ContaServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ContaServiceTest.class,
    ContaMapperTest.class
})
public class UnitSuiteTest {

}
