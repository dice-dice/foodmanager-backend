package com.spire.fridge.inventory.controller;

import com.spire.fridge.inventory.dto.stats.*;
import com.spire.fridge.inventory.entity.User;
import com.spire.fridge.inventory.repository.UserRepository;
import com.spire.fridge.inventory.security.services.UserDetailsImpl;
import com.spire.fridge.inventory.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final UserRepository userRepository;

    @Autowired
    public StatsController(StatsService statsService, UserRepository userRepository) {
        this.statsService = statsService;
        this.userRepository = userRepository;
    }

    /**
     * ダッシュボード用の概要統計を取得
     * GET /api/stats/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<OverviewStatsDTO> getOverviewStats(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        OverviewStatsDTO stats = statsService.getOverviewStats(currentUser);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * 賞味期限カレンダー用データを取得
     * GET /api/stats/expiration-calendar?month=2026-01
     */
    @GetMapping("/expiration-calendar")
    public ResponseEntity<List<ExpirationCalendarDTO>> getExpirationCalendar(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String month
    ) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        YearMonth targetMonth;
        if (month != null && !month.isEmpty()) {
            targetMonth = YearMonth.parse(month);
        } else {
            targetMonth = YearMonth.now();
        }

        List<ExpirationCalendarDTO> calendar = statsService.getExpirationCalendar(currentUser, targetMonth);
        return new ResponseEntity<>(calendar, HttpStatus.OK);
    }

    /**
     * 登録トレンドデータを取得
     * GET /api/stats/trend?period=weekly&range=4
     */
    @GetMapping("/trend")
    public ResponseEntity<TrendDataDTO> getTrendData(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "weekly") String period,
            @RequestParam(defaultValue = "4") int range
    ) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        TrendDataDTO trend = statsService.getTrendData(currentUser, period, range);
        return new ResponseEntity<>(trend, HttpStatus.OK);
    }

    /**
     * カテゴリ別の食材数を取得
     * GET /api/stats/category-breakdown
     */
    @GetMapping("/category-breakdown")
    public ResponseEntity<List<CategoryBreakdownDTO>> getCategoryBreakdown(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<CategoryBreakdownDTO> breakdown = statsService.getCategoryBreakdown(currentUser);
        return new ResponseEntity<>(breakdown, HttpStatus.OK);
    }
}
