/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package evan.wang.parquet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import parquet.column.Encoding;
import parquet.column.ParquetProperties.WriterVersion;
import parquet.example.data.Group;
import parquet.example.data.simple.SimpleGroupFactory;
import parquet.hadoop.ParquetReader;
import parquet.hadoop.ParquetWriter;
import parquet.hadoop.example.GroupReadSupport;
import parquet.hadoop.example.GroupWriteSupport;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.io.api.Binary;
import parquet.schema.MessageType;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static parquet.column.Encoding.*;
import static parquet.column.ParquetProperties.WriterVersion.PARQUET_1_0;
import static parquet.column.ParquetProperties.WriterVersion.PARQUET_2_0;
import static parquet.schema.MessageTypeParser.parseMessageType;

public class TestParquetWriterNewPage {

    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        Path root = new Path("/user/wangsy/tests/TestParquetWriter/");
        FileSystem fs = root.getFileSystem(conf);
        if (fs.exists(root)) {
            fs.delete(root, true);
        }
        fs.mkdirs(root);
        MessageType schema = parseMessageType(
                "message test { "
                        + "required binary binary_field; "
                        + "required int32 int32_field; "
                        + "required int64 int64_field; "
                        + "required boolean boolean_field; "
                        + "required float float_field; "
                        + "required double double_field; "
                        + "required fixed_len_byte_array(3) flba_field; "
                        + "required int96 int96_field; "
                        + "optional binary null_field; "
                        + "} ");
        GroupWriteSupport.setSchema(schema, conf);
        SimpleGroupFactory f = new SimpleGroupFactory(schema);
        Map<String, Encoding> expected = new HashMap<>();
        expected.put("10-" + PARQUET_1_0, PLAIN_DICTIONARY);
        expected.put("1000-" + PARQUET_1_0, PLAIN);
        expected.put("10-" + PARQUET_2_0, RLE_DICTIONARY);
        expected.put("1000-" + PARQUET_2_0, DELTA_BYTE_ARRAY);
        for (int modulo : asList(10, 1000)) {
            for (WriterVersion version : WriterVersion.values()) {
                Path file = new Path(root, version.name() + "_" + modulo);
                ParquetWriter<Group> writer = new ParquetWriter<>(
                        file,
                        new GroupWriteSupport(),
                        CompressionCodecName.UNCOMPRESSED,
                        //blockSize; pageSize; dictionaryPageSize; enableDictionary; validation
                        1024, 1024, 512, true, false, version, conf);
                for (int i = 0; i < 1000; i++) {
                    writer.write(
                            f.newGroup()
                                    .append("binary_field", "test" + (i % modulo))
                                    .append("int32_field", 32)
                                    .append("int64_field", 64L)
                                    .append("boolean_field", true)
                                    .append("float_field", 1.0f)
                                    .append("double_field", 2.0d)
                                    .append("flba_field", "foo")
                                    .append("int96_field", Binary.fromByteArray(new byte[12])));
                }
                writer.close();
                long readTimeStart = System.currentTimeMillis();
                ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), file).withConf(conf).build();
                Group group;
                while ((group = reader.read()) != null) {
                    System.out.print("binary_field: " + group.getBinary("binary_field", 0).toStringUsingUTF8());
                    System.out.print(" int32_field: " + group.getInteger("int32_field", 0));
                    System.out.print(" int64_field: " + group.getLong("int64_field", 0));
                    System.out.print(" boolean_field: " + group.getBoolean("boolean_field", 0));
                    System.out.print(" float_field: " + group.getFloat("float_field", 0));
                    System.out.print(" double_field: " + group.getDouble("double_field", 0));
                    System.out.print(" flba_field: " + group.getBinary("flba_field", 0).toStringUsingUTF8());
                    System.out.print(" int96_field: " + group.getInt96("int96_field", 0));
                    System.out.println(" null_field: " + group.getFieldRepetitionCount("null_field"));
                }
                System.out.println("耗时：  " + (System.currentTimeMillis() - readTimeStart));
                reader.close();
/*              ParquetMetadata footer = readFooter(conf, file, NO_FILTER);
                for (BlockMetaData blockMetaData : footer.getBlocks()) {
                    for (ColumnChunkMetaData column : blockMetaData.getColumns()) {
                        if (column.getPath().toDotString().equals("binary_field")) {
                            String key = modulo + "-" + version;
                            Encoding expectedEncoding = expected.get(key);
                            System.out.println(key + ":" + column.getEncodings() + " should contain " + expectedEncoding + " " +
                                    column.getEncodings().contains(expectedEncoding));
                        }
                    }
                }*/
            }
        }
    }
}


/**
 *
 读取耗时
   UNCOMPRESSED: 88 84 88 176 97
   SNAPPY：104 91 101 104 90
   GZIP： 86 91 112 147 173
   LZO：
 */
