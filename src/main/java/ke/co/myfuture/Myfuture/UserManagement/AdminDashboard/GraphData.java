package ke.co.myfuture.Myfuture.UserManagement.AdminDashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GraphData {

    private String id;
    private String title;
    private List<String> xValues; // dates as strings
    private List<BigDecimal> yValues;
    private double minY;
    private double maxY;
    private String xLabel;
    private String yLabel;

    // Constructors, getters, setters
}
