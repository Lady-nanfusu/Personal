package cn.edu.zucc.personplan.comtrol.example;

import cn.edu.zucc.personplan.itf.IUserManager;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DbException;
import cn.edu.zucc.personplan.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class ExampleUserManager implements IUserManager {

	@Override
	public BeanUser reg(String userid, String pwd,String pwd2) throws BaseException {
		// TODO Auto-generated method stub
		if(userid==null || "".equals(userid)) throw new BusinessException("账号不能为空");
		if(pwd==null || "".equals(pwd)) throw new BusinessException("密码不能为空");
		if(!pwd.equals(pwd2) ) throw new BusinessException("两次密码输入不一样");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			
			String sql = "select user_id from tbl_user where user_id = ? ";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, userid);
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				rs.close();
				pst.close();
				throw new BusinessException("用户已存在");
			}
			rs.close();
			pst.close();
			sql = "insert into tbl_user(user_id,user_pwd,register_time) values(?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, userid);
			pst.setString(2, pwd);
			pst.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.execute();
			BeanUser user = new BeanUser();
			user.setRegister_time(new java.util.Date());
			user.setUser_id(userid);
			
			return user;
		}catch(SQLException ex){
			throw new DbException(ex);
		
		}finally {
			if(conn!=null) {
				try {
					conn.close();
					
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
		

	}

	
	@Override
	public BeanUser login(String userid, String pwd) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn = null;
		BeanUser user = null;
		if(pwd == null) {
			
		}
		try {
			conn = DBUtil.getConnection();
			String sql = "select register_time "
					+" from tbl_user where user_id = ? and user_pwd = ?" ;
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, userid);
			pst.setString(2, pwd);
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				user = new BeanUser();
				user.setUser_id(userid);
				user.setRegister_time(rs.getTimestamp(1));
				return user;		
			}
			
			else{
				throw new BusinessException("用户不存在或密码错误");
			}
			
		}catch(SQLException ex){
			throw new DbException(ex);
		
		}finally {
			if(conn!=null) {
				try {
					conn.close();
					
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}


	@Override
	public void changePwd(BeanUser user, String oldPwd, String newPwd,
			String newPwd2) throws BaseException {
		// TODO Auto-generated method stub
		
	}

}
