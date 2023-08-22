package hh.Service;

import java.sql.SQLException;
import java.util.List;

public interface IGenericService<T,E>{
    List<T> findall() throws SQLException;
    void  save(T t) throws SQLException;
    T findById (E e) throws SQLException;
    void  deleteById(E e) throws SQLException;
}
