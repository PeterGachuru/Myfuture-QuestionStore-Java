package ke.co.myfuture.Myfuture.UserManagement.AdminDashboard;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraphService {

    public GraphData buildGraph(
            String id,
            String title,
            List<LocalDate> dates,
            List<BigDecimal> values,
            String xLabel,
            String yLabel
    ) {

        List<String> formattedDates = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());

        return buildTimeSeries(id, title, formattedDates, values, xLabel, yLabel);
    }

    public GraphData buildTimeSeries(
            String id,
            String title,
            List<String> labels,
            List<BigDecimal> values,
            String xLabel,
            String yLabel
    ) {
        double min = values.stream().mapToDouble(BigDecimal::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(BigDecimal::doubleValue).max().orElse(100);

        // Add padding (optional)
        min = Math.max(0, min - 5);
        max = max + 5;

        GraphData graph = new GraphData();
        graph.setId(id);
        graph.setTitle(title);
        graph.setXValues(labels);
        graph.setYValues(values);
        graph.setMinY(min);
        graph.setMaxY(max);
        graph.setXLabel(xLabel);
        graph.setYLabel(yLabel);

        return graph;
    }

    public GraphData fromCountPerDay(
            String id,
            String title,
            List<Object[]> results,
            String xLabel,
            String yLabel
    ) {
        List<String> dates = new ArrayList<>();
        List<BigDecimal> counts = new ArrayList<>();

        for (Object[] row : results) {
            dates.add(row[0].toString());

            Object value = row[1];
            Number number;
            if (value instanceof BigDecimal) {
                number = ((BigDecimal) value);
            } else {
                number = ((Number) value);
            }

            counts.add(value instanceof BigDecimal ? (BigDecimal) value : BigDecimal.valueOf(number.doubleValue()));
        }

        return buildGraph(id, title,
                dates.stream().map(LocalDate::parse).toList(),
                counts,
                xLabel,
                yLabel
        );
    }
}
