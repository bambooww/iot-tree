package org.iottree.core.store.gdb.xorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Jason Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface XORMProperty
{

	String name();
	

	String title() default "" ;


	boolean is_transient() default false;
	

	boolean has_col() default false; // ���û��ָ��,�������ݱ�����XmlData�ṹ��


	String default_str_val() default "";//ȱʡ�ַ���ֵ
	

	boolean is_pk() default false;// �Ƿ�������
	
	
	String auto_id_prefix() default ""; //�������Զ��ַ���ʱ����������idǰ׺
	

	boolean is_auto() default false;//�Ƿ��Զ�ֵ ���������Ч,�ж��Ƿ������������


	long auto_value_start() default -1;
	

	boolean has_idx() default false;// �Ƿ������� ǰ��has_col-true����,��ʾ�ж�������
	

	String idx_name() default "" ;
	

	boolean is_unique_idx() default false;//


	boolean nullable() default true ;//


	boolean has_fk() default false;// 


	String fk_table() default "";// 


	String fk_column() default "";// 


	boolean fk_base_class() default false;
	
	

	String ref_pk_table() default "";


	int max_len() default -1; // . ǰ��is_seperate = true
	

	boolean auto_truncate() default false;
	

	int order_num() default 1000; //˳���
	

	boolean store_as_file() default false;
	

	boolean read_on_demand() default false;
	

	boolean update_as_single() default false;
	

	boolean support_auto() default false ;
	

	String bean_get_set_name() default "" ;
	

	String value_options() default "" ;
	


	boolean is_auto_update_dt() default false;

	String idx_for_json_col() default "";
}
