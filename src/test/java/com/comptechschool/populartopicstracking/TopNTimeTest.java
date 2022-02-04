package com.comptechschool.populartopicstracking;

import com.comptechschool.populartopicstracking.operator.topn.EntityTrigger;
import com.comptechschool.populartopicstracking.operator.topn.processimpl.AdvancedEntityProcessFunction;
import com.comptechschool.populartopicstracking.operator.topn.processimpl.DefaultEntityProcessFunction;
import com.comptechschool.populartopicstracking.source.DataSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;

import java.time.Duration;

public class TopNTimeTest {

    int n = 10;


    @Test
    public void defaultTopNTest() throws Exception { //200 ms


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        initProperties(env);

        env.addSource(new DataSource(50000L))
                //.assignTimestampsAndWatermarks(new EntityAssignerWaterMarks(Time.seconds(5)))
                .assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(20)))
                .windowAll(TumblingEventTimeWindows.of(Time.seconds(30)))
                .allowedLateness(Time.seconds(20))
                .trigger(new EntityTrigger(100000))//clean up the window data
                .process(new DefaultEntityProcessFunction(n));

        env.execute("Real-time entity topN");
    }

    @Test
    public void advancedTopNTest() throws Exception { //250 ms


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        initProperties(env);

        env.addSource(new DataSource(50000L))
                //.assignTimestampsAndWatermarks(new EntityAssignerWaterMarks(Time.seconds(5)))
                .assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(20)))
                .windowAll(TumblingEventTimeWindows.of(Time.seconds(30)))
                .allowedLateness(Time.seconds(20))
                .trigger(new EntityTrigger(1000000))//clean up the window data
                .process(new AdvancedEntityProcessFunction(n));

        env.execute("Real-time entity topN");
    }


    private void initProperties(StreamExecutionEnvironment env) {
        //Global parallelism
        env.setParallelism(1);

        //checkpoint per minute
        env.enableCheckpointing(1000 * 60 * 10);

        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //Default - EventTime
        //Restart three times after failure, each interval of 20s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, org.apache.flink.api.common.time.Time.seconds(20)));
        //Set the maximum parallelism of checkpoints
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //Do not delete the save point data even if you manually cancel the task
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.
                ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    }
}
