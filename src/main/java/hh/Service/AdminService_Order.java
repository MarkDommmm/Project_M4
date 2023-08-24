package hh.Service;

import hh.Model.Customer;
import hh.Model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService_Order implements IGenericService<Order, Integer> {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Order> findall() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<Order> orders = new ArrayList<>();
        try {
            CallableStatement callSt = conn.prepareCall("{call GetOrder}");
            ResultSet rs = callSt.executeQuery();
            while (rs.next()) {
                Order p = new Order();
                p.setIdOrder(rs.getInt("id"));
                p.setId_customer(rs.getInt("id_customer"));
                p.setName(rs.getString("name"));
                p.setAddress(rs.getString("address"));
                p.setPhone(rs.getString("phone"));
                p.setDescription(rs.getString("description"));
                p.setOrder_at(rs.getDate("order_at"));
                p.setTotal_price(rs.getFloat("total_price"));
                p.setStatus(rs.getBoolean("status"));
                orders.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return orders;
    }

    @Override
    public void save(Order order) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
                CallableStatement callSt = conn.prepareCall("{call UpdateOrder(?,?)}");
                callSt.setInt(1, order.getIdOrder());
                callSt.setBoolean(2, order.isStatus());
                callSt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Order findById(Integer integer) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Order p = null;
        try {
            CallableStatement callSt = conn.prepareCall("{call FindOrder(?)}");
            callSt.setInt(1, integer);
            ResultSet rs = callSt.executeQuery();
            while (rs.next()) {
                p = new Order();
                p.setIdOrder(rs.getInt("id"));
                p.setId_customer(rs.getInt("id_customer"));
                p.setName(rs.getString("name"));
                p.setAddress(rs.getString("address"));
                p.setPhone(rs.getString("phone"));
                p.setDescription(rs.getString("description"));
                p.setOrder_at(rs.getDate("order_at"));
                p.setTotal_price(rs.getFloat("total_price"));
                p.setStatus(rs.getBoolean("status"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return p;
    }

    @Override
    public void deleteById(Integer integer) {

    }
}
