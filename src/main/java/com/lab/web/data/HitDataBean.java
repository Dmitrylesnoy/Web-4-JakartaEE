package com.lab.web.data;

import java.io.Serializable;
import java.util.List;

import com.lab.web.database.DataAccessStrategy;
import com.lab.web.database.JDBCDataAccess;

import jakarta.ejb.Singleton;

@Singleton
public class HitDataBean implements Serializable {
    private DataAccessStrategy dataAccessStrategy;

    public HitDataBean() {
        this.dataAccessStrategy = new JDBCDataAccess();
    }

    public List<PointData> getData(Long userId) {
        return dataAccessStrategy.getAllPoints(userId);
    }

    public void addPoint(PointData point) {
        dataAccessStrategy.addPoint(point);
    }

    public String getDataAsJson(Long userId) {
        List<PointData> data = getData(userId);
        if (data.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < data.size(); i++) {
            PointData point = data.get(i);
            json.append("{");
            json.append("\"x\":").append(point.getX()).append(",");
            json.append("\"y\":").append(point.getY()).append(",");
            json.append("\"r\":").append(point.getR()).append(",");
            json.append("\"hit\":").append(point.isHit()).append(",");
            json.append("\"execTime\":").append(point.getExecTime()).append(",");
            json.append("\"date\":\"").append(point.getdateFormatted()).append("\"");
            json.append("}");

            if (i < data.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}
