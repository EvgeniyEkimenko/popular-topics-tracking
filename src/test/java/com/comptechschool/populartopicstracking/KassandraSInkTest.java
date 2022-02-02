package com.comptechschool.populartopicstracking;

import com.comptechschool.populartopicstracking.function.ListToTupleFlatMapper;
import com.comptechschool.populartopicstracking.operator.topn.EntityTrigger;
import com.comptechschool.populartopicstracking.operator.topn.processimpl.DefaultEntityProcessFunction;
import com.comptechschool.populartopicstracking.source.DataSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.junit.Test;

import java.time.Duration;

public class KassandraSInkTest {

    @Test
    public void topNTest() throws Exception {
        int n = 3;

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        initProperties(env);

        DataStream<Tuple3<Long, Long, String>> result = env.addSource(new DataSource(10000L))
                //.assignTimestampsAndWatermarks(new EntityAssignerWaterMarks(Time.seconds(5)))
                .assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(20)))
                .windowAll(TumblingEventTimeWindows.of(Time.seconds(30)))
                .allowedLateness(Time.seconds(20))
                .trigger(new EntityTrigger(50000))//clean up the window data
                .process(new DefaultEntityProcessFunction(n))
                .flatMap(new ListToTupleFlatMapper())
                .returns(TypeInformation.of(new TypeHint<Tuple3<Long, Long, String>>() {
                }));

        /**/



        CassandraSink.addSink(result)
                .setQuery("INSERT INTO example.testdb(id, frequency, action) values (?, ?, ?);")
                .setHost("127.0.0.1")
                .build()
                .name("cassandra Sink")
                .disableChaining();

        env.execute("kafka- kafka_version source, cassandra-4.1.0 sink, tuple3");
    }

    private void initProperties(StreamExecutionEnvironment env) {


        env.setParallelism(5);
        env.enableCheckpointing(1000 * 60 * 10);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //Restart three times after failure, each interval of 20s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, org.apache.flink.api.common.time.Time.seconds(20)));
        //Set the maximum parallelism of checkpoints
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //Do not delete the save point data even if you manually cancel the task
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.
                ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    }
}
