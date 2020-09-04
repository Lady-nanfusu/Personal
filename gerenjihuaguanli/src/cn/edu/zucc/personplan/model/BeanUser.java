package cn.edu.zucc.personplan.model;
import java.util.*;

public class BeanUser {
	public static BeanUser currentLoginUser=null;
	Date register_time =null;
	String user_id = null;
	public void setRegister_time(Date register_time){
		this.register_time=register_time;
	}
	public Date getRegister_time() {
		return register_time;
	}
	public void setUser_id(String user_id){
		this.user_id=user_id;
	}
	public String getUser_id() {
		return user_id;
	}
}
