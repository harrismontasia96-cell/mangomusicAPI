package com.mangomusic.service;

import com.mangomusic.dao.ReportsDao;
import com.mangomusic.model.ReportResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportsService {

    private final ReportsDao reportsDao;

    public ReportsService(ReportsDao reportsDao) {
        this.reportsDao = reportsDao;
    }

    public List<ReportResult> getDailyActiveUsersReport() {
        return reportsDao.getDailyActiveUsersReport();
    }

    public List<ReportResult> getTopAlbumsThisMonthReport() {
        return reportsDao.getTopAlbumsThisMonthReport();
    }

    public List<ReportResult> getGenrePopularityReport() {
        return reportsDao.getGenrePopularityReport();
    }

    public List<ReportResult> getTopArtistsReport() {
        return reportsDao.getTopArtistsReport();
    }

    public ReportResult getMangoMusicMapped(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return reportsDao.getMangoMusicMapped(userId);
    }
}