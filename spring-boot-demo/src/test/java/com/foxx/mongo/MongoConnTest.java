package com.foxx.mongo;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * 作者：wangsy
 * 日期：2016/6/17 20:05
 * 描述：
 */
public class MongoConnTest {
    private MongoClient mongoClient;
    private DB db;
    private DBCollection dbCollection;

    @Before
    public void before() {
        try {
            mongoClient = new MongoClient("127.0.0.1", 27017);
            db = mongoClient.getDB("crawler");
            dbCollection = db.getCollection("sites");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        if (mongoClient != null) {
            mongoClient.close();
        }
        db = null;
    }

    //@Test
    public void testInsert() {
        /**
         * 插入： {
         *         "url": "org.mongodb",
         *         "tags" : ["database", "open-source",
         *         "attrs": {"lastAddress": new Date(), "pingtime": 20}
         *       }
         */
        DBObject doc = new BasicDBObject();
        String[] tags = {"database", "open-source"};
        doc.put("url", "org.mongodb");
        doc.put("tags", tags);
        DBObject attrs = new BasicDBObject();
        attrs.put("lastAddress", new Date());
        attrs.put("pingtime", 20);
        doc.put("attrs", attrs);
        doc.put("value", 10);
        WriteResult result = dbCollection.insert(doc);

        System.out.println(">>>>>>>>>>>>>>>>>>Initial document:n");
        System.out.println(doc.toString());
        System.out.println(">>>>>>>>>>>>>>>>>>commit insert document: " + result.getN());
    }

    //@Test
    public void testUpdate() {
        DBObject id = new BasicDBObject("_id", new ObjectId("576b9668052491adbf2c4936"));
        //修改value = 30
        DBObject update = new BasicDBObject("$set", new BasicDBObject("value", 30));
        //修改value = value + 30
        //DBObject update = new BasicDBObject("$inc",new BasicDBObject("value", 30));
        WriteResult result = dbCollection.update(id, update);
        System.out.println(">>>>>>>>>>>>>>>>>>commit update document:  " + result.getN());
    }

    //@Test
    public void testRemove() {
        DBObject id = new BasicDBObject("_id", new ObjectId("576b8c0e0524a637b562c158"));
        WriteResult result = dbCollection.remove(id);
        System.out.print(">>>>>>>>>>>>>>>>>>>commit remove document:  " + result.getN());
    }

    @Test
    public void testFindAll() {
        DBCursor cursor = dbCollection.find();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            System.out.println(object);
        }
    }

}
