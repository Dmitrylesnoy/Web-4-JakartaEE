package com.lab.web.data;

import java.io.Serializable;
import java.util.List;

import com.lab.web.database.repository.PointsRepository;
import com.lab.web.database.service.JDBCService;

import jakarta.ejb.Singleton;

@Singleton
public class HitDataBean implements Serializable {
    private transient PointsRepository pointsRepo;

    public HitDataBean() {
        this.pointsRepo = new JDBCService();
    }

    public List<PointData> getData(Long userId) {
        return pointsRepo.getAllPoints(userId);
    }

    public void addPoint(PointData point) {
        pointsRepo.addPoint(point);
    }
}
