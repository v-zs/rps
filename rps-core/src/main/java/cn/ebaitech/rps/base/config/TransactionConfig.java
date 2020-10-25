package cn.ebaitech.rps.base.config;

import cn.ebaitech.rps.base.expend.DynamicDataSource;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.*;

import java.util.*;

@Configuration
public class TransactionConfig {
    public static List<String> readonlyPatterns = new ArrayList();
    public static List<String> writeablePatterns = new ArrayList();

    public static final String POINTCUT_EXPRESSION = "execution(* cn.ebaitech.rps.service.*.*(..))";

    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("txAdvice")
    public TransactionInterceptor transactionInterceptor(TransactionManager transactionManager) {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

        RuleBasedTransactionAttribute supportsTx = new RuleBasedTransactionAttribute();
        supportsTx.setReadOnly(true);
        supportsTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);

        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Map<String, TransactionAttribute> nameMap = new HashMap();

        readonlyPatterns.add("get*");
        readonlyPatterns.add("page*");
        readonlyPatterns.add("search*");
        readonlyPatterns.add("select*");
        for (String methodPattern : readonlyPatterns) {
            nameMap.put(methodPattern, supportsTx);
        }

        writeablePatterns.add("insert*");
        writeablePatterns.add("update*");
        writeablePatterns.add("delete*");
        for (String methodPattern : writeablePatterns) {
            nameMap.put(methodPattern, requiredTx);
        }

        source.setNameMap(nameMap);
        return new TransactionInterceptor(transactionManager, source);
    }

    @Bean
    public PointcutAdvisor pointcutAdvisor(TransactionInterceptor txAdvice) {
        AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        pointcutAdvisor.setExpression(POINTCUT_EXPRESSION);
        pointcutAdvisor.setAdvice(txAdvice);
        return pointcutAdvisor;
    }
}
