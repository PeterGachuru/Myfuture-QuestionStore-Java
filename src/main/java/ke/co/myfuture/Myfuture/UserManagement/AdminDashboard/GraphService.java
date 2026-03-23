package ke.co.myfuture.Myfuture.UserManagement.AdminDashboard;

import org.springframework.stereotype.Service;

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
            List<Integer> values,
            String xLabel,
            String yLabel
    ) {

        List<String> formattedDates = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());

        int min = values.stream().min(Integer::compareTo).orElse(0);
        int max = values.stream().max(Integer::compareTo).orElse(100);

        // Add padding (optional)
        min = Math.max(0, min - 5);
        max = max + 5;

        GraphData graph = new GraphData();
        graph.setId(id);
        graph.setTitle(title);
        graph.setXValues(formattedDates);
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
        List<Integer> counts = new ArrayList<>();

        for (Object[] row : results) {
            dates.add(row[0].toString());
            counts.add(((Number) row[1]).intValue());
        }

        return buildGraph(id, title,
                dates.stream().map(LocalDate::parse).toList(),
                counts,
                xLabel,
                yLabel
        );
    }
}