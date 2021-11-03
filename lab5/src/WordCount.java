import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

    public static class FrequencySortMap extends  Mapper<Object, Text, IntWritable, Text>
    {
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException
        {
            String line = value.toString();
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            while(stringTokenizer.hasMoreTokens())
            {
                String token = stringTokenizer.nextToken();//word
                if(stringTokenizer.hasMoreTokens()){
                    String fr = stringTokenizer.nextToken();
                    int number = Integer.parseInt(fr);
                    context.write(new IntWritable(number), new Text(token));
                }
                else {
                    int number = Integer.parseInt(token);
                    context.write(new IntWritable(number), new Text(""));
                }
            }
        }
    }
    public static class FrequencySortReducer
            extends Reducer<IntWritable, Text, IntWritable, Text> {
        private Text result = new Text();

        public void reduce(IntWritable key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {
            String s = "";
            List<String> list = new ArrayList<>();
            for (Text val : values) {
                list.add(val.toString());//s+=val.toString()+ " "
            }
            for (int i = 0; i < list.size(); i++) {
                s+=list.get(i);
                if(i!=list.size()-1)s+=" ";
            }
            result.set(s);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "frequency sort");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(FrequencySortMap.class);
        job.setCombinerClass(FrequencySortReducer.class);
        job.setReducerClass(FrequencySortReducer.class);
        job.setOutputValueClass(Text.class);
        job.setOutputKeyClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}