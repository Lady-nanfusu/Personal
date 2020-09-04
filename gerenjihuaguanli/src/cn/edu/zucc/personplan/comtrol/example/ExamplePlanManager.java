package cn.edu.zucc.personplan.comtrol.example;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

import java.sql.Connection;
import java.sql.SQLException;

public class ExamplePlanManager implements IPlanManager {

	@Override
	public BeanPlan addPlan(String name) throws BaseException {
		// TODO Auto-generated method stub
		if(name==null || "".equals(name)) throw new BusinessException("计划名称必须提供");
		
		Connection conn =null;
		try {
			conn=DBUtil.getConnection();
			String user_id=BeanUser.currentLoginUser.getUser_id();
			int plan_ord=0;
			
			String sql="select plan_id from tbl_plan where user_id = ? and plan_name = ?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, user_id);
			java.sql.ResultSet rs = pst.executeQuery();
			
			if(rs.next()) {
				rs.close();
				pst.close();
				throw new BusinessException("同名计划已存在");
			}
			
			else{
				plan_ord = 1;
			}
			rs.close();
			pst.close();
			
			sql = "select max(plan_ord) from tbl_plan where user_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setString(1, user_id);
			rs = pst.executeQuery();
			
			if(rs.next()) {
				plan_ord = rs.getInt(1)+1;		
			}
			
			else{
				plan_ord = 1;
			}
			rs.close();
			pst.close();
			sql = "insert into tbl_plan("
					+ "     user_id,plan_order,plan_name"
					+" create_time,step_count,start_step_count,finished_step_count"
					+" value(?,?,?,?,0,0,0)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, user_id);
			pst.setInt(2, plan_ord);
			pst.setString(3, name);
			pst.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.execute();
			BeanPlan p = new BeanPlan();
			sql = "select max(plan_id) from tbl_plan where user_id = ?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, user_id);
			rs = pst.executeQuery();
			if(rs.next()) {
				int pid = rs.getInt(1);		
			}
			
			else{
				plan_ord = 1;
			}
			rs.close();
			pst.close();
			//p.set     属性设置
			return p;
			
			
		}catch(SQLException ex){
			ex.printStackTrace();
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
	public List<BeanPlan> loadAll() throws BaseException {
		List<BeanPlan> result=new ArrayList<BeanPlan>();
		Connection conn =null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from tbl_plan where user_id = ? order by plan_ord";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1,BeanUser.currentLoginUser.getUser_id());
			java.sql.ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				BeanPlan p=new BeanPlan();
				result.add(p);
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
		
		BeanPlan p=new BeanPlan();
		result.add(p);
		return result;
	}

	@Override
	public void deletePlan(BeanPlan plan) throws BaseException {
		int plan_id=1;
		
		Connection conn =null;
		try {
			conn=DBUtil.getConnection();
			String sql="select count(*) from tbl_step where plan_id = "+plan_id;
			java.sql.Statement st = conn.createStatement();
			java.sql.ResultSet rs = st.executeQuery(sql);
			
			if(rs.next()) {
				if(rs.getInt(1)>0) {
				rs.close();
				st.close();
				throw new BusinessException("该计划已存在步骤，不能删除");
				}
			}
			
			rs.close();
			sql="select plan_ord,user_id form tbl_plan where plan_id = "+plan_id;
			rs=st.executeQuery(sql);
			int plan_ord=0;
			String plan_user_id=null;
			
			if(rs.next()) {
				plan_ord=rs.getInt(1);
				plan_user_id=rs.getString(2);
				
			}else {
				throw new BusinessException("该计划不存在");
			}
			rs.close();
			if(!BeanUser.currentLoginUser.getUser_id().equals(plan_user_id)) {
				st.close();
				throw new BusinessException("不能删除别人的计划");
			}
			
			sql = "delete from tbl_plan where plan id = ?"+plan_id;
			st.execute(sql);
			st.close();
			sql = "update tbl_plan set plan_ord = plan_ord -1 where user_id = ? and plan_ord>"+plan_ord;
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1,plan_user_id);
			pst.execute();
			
			
		}catch(SQLException ex){
			ex.printStackTrace();
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

}
