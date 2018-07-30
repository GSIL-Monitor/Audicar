package xmpp.d3View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MZH
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface D3View {
	int id() default 0;
	String click() default "";
	String longClick() default "";
	String itemClick() default "";
	String itemLongClick() default "";
}