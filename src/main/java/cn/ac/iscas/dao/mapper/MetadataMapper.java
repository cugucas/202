package cn.ac.iscas.dao.mapper;

import cn.ac.iscas.dao.enity.Metadata;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * @description: 实验表Mapper
 * @author LJian
 * @date: 2022/4/6 15:23
 */
@Mapper
public interface MetadataMapper {
    @Select({
            " SELECT ",
            " id, dbName, tableName, major, json ",
            " FROM t_ship"
    })
    @Results({
            @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER),
            @Result(column = "dbName", property = "database", jdbcType = JdbcType.VARCHAR),
            @Result(column = "tableName", property = "table", jdbcType = JdbcType.VARCHAR),
            @Result(column = "major", property = "major", jdbcType = JdbcType.VARCHAR),
            @Result(column = "json", property = "json", jdbcType = JdbcType.VARCHAR)
    })
    List<Metadata> selectAll();


    @Select({
            " SELECT ",
            " id, dbName, tableName, major, json ",
            " FROM t_ship ",
            " WHERE major = #{major, jdbcType=VARCHAR}"
    })
    @Results({
            @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER),
            @Result(column = "dbName", property = "database", jdbcType = JdbcType.VARCHAR),
            @Result(column = "tableName", property = "table", jdbcType = JdbcType.VARCHAR),
            @Result(column = "major", property = "major", jdbcType = JdbcType.VARCHAR),
            @Result(column = "json", property = "json", jdbcType = JdbcType.VARCHAR)
    })
    List<Metadata> selectByMajor(String major);

}
