package com.github.llq.tx;

import java.lang.annotation.*;

/**
 * @author luqi.liu
 * @date 2023/8/24 15:01
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MultiTransactional {

    String[] value();

//    Propagation propagation() default Propagation.REQUIRED;
//
//    Isolation isolation() default Isolation.DEFAULT;
//
//    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
//
//    String timeoutString() default "";
//
//    boolean readOnly() default false;

    Class<? extends Throwable>[] rollbackFor() default {};

    String[] rollbackForClassName() default {};

    Class<? extends Throwable>[] noRollbackFor() default {};

    String[] noRollbackForClassName() default {};

}
