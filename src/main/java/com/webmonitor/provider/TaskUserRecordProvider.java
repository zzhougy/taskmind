package com.webmonitor.provider;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webmonitor.entity.po.TaskUserRecord;
import com.webmonitor.mapper.TaskUserRecordMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskUserRecordProvider extends ServiceImpl<TaskUserRecordMapper, TaskUserRecord> {
}
