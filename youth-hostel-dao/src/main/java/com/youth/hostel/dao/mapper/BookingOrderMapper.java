package com.youth.hostel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youth.hostel.entity.po.BookingOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预定订单Mapper接口
 */
@Mapper
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {
}
