package com.saceone.tfg.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.saceone.tfg.Classes.Registro;
import com.saceone.tfg.R;
import com.saceone.tfg.Utils.MyDB;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ASUS on 14/04/2016.
 */
public class Graficas extends AppCompatActivity{

    MyDB db;

    Spinner sp_tipo;
    Spinner sp_periodo;
    Spinner sp_grafica;

    Button btn_ver_grafica;

    String tipo;
    String periodo;
    String grafica;

    int[] colores = {Color.rgb(241,196,15), Color.rgb(230,126,34), Color.rgb(231,76,60)};
    String[] tipo_consumicion = {"Desayunos", "Comidas", "Cenas"};
    String[] month = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
    String[] day = {"L","M","X","J","V","S","D"};
    String[] diames = {"1","2","3","4","5","6","7","8","9",
                    "10","11","12","13","14","15","16","17","18","19",
                    "20","21","22","23","24","25","26","27","28","29",
                    "30","31"};
    String[] desayuno = {"7:00","7:30","8:00","8:30","9:00","9:30","10:00","10:30","11:00","11:30","12:00","12:30"};
    String[] comida = {"12:45","13:00","13:15","13:30","13:45","14:00","14:15","14:30","14:45","15:00","15:15","15:30","15:45","16:00","16:15"};
    String[] cena = {"20:15","20:30","20:45","21:00","21:15","21:30","21:45","22:00","22:15","22:30","22:45","23:00","23:15"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graficas);

        db = new MyDB(Graficas.this);

        tipo = "Desayunos";
        periodo = "Hoy";
        grafica = "Líneas";

        sp_tipo = (Spinner)findViewById(R.id.spinner_grafica_tipo);
        setSpinner(sp_tipo,R.array.grafica_tipo);
        sp_tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        sp_periodo = (Spinner)findViewById(R.id.spinner_grafica_periodo);
        setSpinner(sp_periodo,R.array.grafica_periodo);
        sp_periodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                periodo = parent.getItemAtPosition(position).toString();
                if (periodo.equals("Seleccionar día...")) {
                    if(grafica.equals("Tarta")){
                        setSpinner(sp_periodo, R.array.grafica_periodo_ifTarta);
                    }
                    else{
                        setSpinner(sp_periodo, R.array.grafica_periodo);
                    }
                    final int[] dia_mes = new int[1];
                    final int[] mes = new int[1];
                    final int[] year = new int[1];
                    final Dialog dialog = new Dialog(Graficas.this);
                    dialog.setTitle("Seleccionar fecha");
                    dialog.setContentView(R.layout.dialog_calendar);
                    CalendarView calendar = (CalendarView) dialog.findViewById(R.id.dialog_calendar_calendar);
                    calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        public void onSelectedDayChange(CalendarView view, int theYear, int month, int dayOfMonth) {
                            dia_mes[0] = dayOfMonth;
                            mes[0] = month + 1; //Enero es 0
                            year[0] = theYear;
                        }
                    });
                    Button btn_cancel = (Button) dialog.findViewById(R.id.btn_dialog_calendar_cancel);
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button btn_submit = (Button) dialog.findViewById(R.id.btn_dialog_calendar_submit);
                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            setGrafica(-1, dia_mes[0], mes[0], year[0]);
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        sp_grafica = (Spinner)findViewById(R.id.spinner_grafica_estilo);
        setSpinner(sp_grafica, R.array.grafica_estilo);
        sp_grafica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                grafica = parent.getItemAtPosition(position).toString();
                //La gráfica Tarta solo la usaré para HOY o para SIEMPRE, y en cualquier caso, para TODAS las consumiciones
                if(grafica.equals("Tarta")){
                    setSpinner(sp_tipo, R.array.grafica_tipo_ifTarta);
                    setSpinner(sp_periodo, R.array.grafica_periodo_ifTarta);
                }
                else{
                    setSpinner(sp_tipo,R.array.grafica_tipo);
                    setSpinner(sp_periodo,R.array.grafica_periodo);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_ver_grafica = (Button)findViewById(R.id.btn_grafica);
        btn_ver_grafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                int dia_mes = now.get(Calendar.DAY_OF_MONTH);
                int dia_semana = now.get(Calendar.DAY_OF_WEEK);
                int mes = now.get(Calendar.MONTH) + 1;;
                int year = now.get(Calendar.YEAR);
                setGrafica(dia_semana,dia_mes,mes,year);
            }
        });
    }

    private void setSpinner(Spinner spinner, int array) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Graficas.this,array, R.layout.sp_15dp_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setGrafica(int dia_semana, int dia_mes, int mes, int year) {
        //EJE X
        List<Integer> x = new ArrayList<Integer>();
        List<String> x_labels = new ArrayList<>();
        int length;
        switch (periodo){
            case "Hoy":
                switch (tipo){
                    case "Desayunos":
                        length=desayuno.length;
                        for(int i=0;i<length;i++) x_labels.add(desayuno[i]);
                            break;
                    case "Comidas":
                        length=comida.length;
                        for(int i=0;i<length;i++) x_labels.add(comida[i]);
                        break;
                    case "Cenas":
                        length=cena.length;
                        for(int i=0;i<length;i++) x_labels.add(cena[i]);
                        break;
                    default:
                        //default en este caso hace las veces de "T0D0"
                        length=tipo_consumicion.length;
                        for(int i=0;i<length;i++) x_labels.add(tipo_consumicion[i]);
                        break;
                }
                break;
            case "Última semana":
                length=day.length;
                for(int i=0;i<length;i++) x_labels.add(day[i]);
                break;
            case "Último mes":
                length=diames.length;
                for(int i=0;i<length;i++) x_labels.add(diames[i]);
                break;
            case "Último año":
                length=month.length;
                for(int i=0;i<length;i++) x_labels.add(month[i]);
                break;
            default:
                //default en este caso hace las veces de "SIEMPRE"
                length=tipo_consumicion.length;
                for(int i=0;i<length;i++) x_labels.add(tipo_consumicion[i]);
                break;
        }
        for(int i=0;i<length;i++){
            x.add(i + 1);
        }

        //EJE Y
        int y_max=0;
        String tabla;
        switch (tipo){
            case "Desayunos":
                tabla="desayunos";
                break;
            case "Comidas":
                tabla="comidas";
                break;
            case "Cenas":
                tabla="cenas";
                break;
            default:
                tabla="todo";
                break;
        }
        List<Integer> y = new ArrayList<Integer>();
        for(int i=0; i<length; i++){
            if((grafica.equals("Tarta"))&&(tipo.equals("Todo"))&&(periodo.equals("Siempre"))){
                List<Registro> registroList = db.getRegistroList();
                int count = 0;
                switch (i){
                    case 0:
                        for(Registro registro : registroList){
                            if(registro.getTabla().equals("desayunos")){
                                count++;
                            }
                        }
                        break;
                    case 1:
                        for(Registro registro : registroList){
                            if(registro.getTabla().equals("comidas")){
                                count++;
                            }
                        }
                        break;
                    default:
                        for(Registro registro : registroList){
                            if(registro.getTabla().equals("cenas")){
                                count++;
                            }
                        }
                        break;
                }
                //Resto los dummy (1 por cada categoria)
                y.add(count-1);
            }
            else{
                y.add(db.getPeriodRegistros(x_labels.get(i), dia_mes, dia_semana,mes,year,tabla));
                if(db.getPeriodRegistros(x_labels.get(i),dia_mes,dia_semana,mes,year,tabla)>y_max){
                    y_max=db.getPeriodRegistros(x_labels.get(i),dia_mes,dia_semana,mes,year,tabla);
                }
            }
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer multirenderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        CategorySeries mSeries = new CategorySeries("");
        DefaultRenderer mRenderer = new DefaultRenderer();

        if (grafica.equals("Tarta")){
            mRenderer.setApplyBackgroundColor(true);
            mRenderer.setBackgroundColor(Color.WHITE);
            mRenderer.setChartTitleTextSize(20);
            mRenderer.setLabelsTextSize(25);
            mRenderer.setLabelsTextSize(25);
            mRenderer.setLabelsColor(Color.BLACK);
            mRenderer.setLegendTextSize(25);
            mRenderer.setZoomButtonsVisible(true);
            mRenderer.setStartAngle(90);
            for (int i = 0; i < length; i++) {
                mSeries.add(tipo_consumicion[i] + ": " + y.get(i), y.get(i));
                SimpleSeriesRenderer sSrenderer = new SimpleSeriesRenderer();
                sSrenderer.setColor(colores[(mSeries.getItemCount() - 1) % colores.length]);
                mRenderer.addSeriesRenderer(sSrenderer);
            }
        }
        else{
            //SERIE
            XYSeries serie = new XYSeries("Consumiciones");
            for(int i=0;i<length;i++){
                serie.add(x.get(i), y.get(i));
            }

            //DATASET
            dataset.addSeries(serie);

            //RENDERER para lineas y barras
            renderer.setColor(Color.BLUE);
            renderer.setPointStyle(PointStyle.CIRCLE);
            renderer.setChartValuesTextSize(28);
            renderer.setFillPoints(true);
            renderer.setLineWidth(2);
            renderer.setDisplayChartValues(true);

            //MULTIRENDERER
            multirenderer.setXLabels(0);
            multirenderer.setXAxisMin(0.5);
            multirenderer.setXAxisMax(x.size() + 0.5);
            multirenderer.setYAxisMin(0);
            multirenderer.setXLabelsPadding(30);
            multirenderer.setYAxisMax(y_max + 1);
            multirenderer.setYLabelsPadding(10);
            multirenderer.setLabelsTextSize(20);
            multirenderer.setAxisTitleTextSize(28);
            multirenderer.setLegendTextSize(1);
            multirenderer.setXLabelsAngle(45);
            multirenderer.setZoomButtonsVisible(true);
            for(int i=0; i<length;i++){
                multirenderer.addXTextLabel(i + 1, x_labels.get(i));
            }
            multirenderer.addSeriesRenderer(renderer);
            multirenderer.setBarSpacing(0.3);
            multirenderer.setApplyBackgroundColor(true);
            multirenderer.setBackgroundColor(Color.WHITE);
            multirenderer.setXAxisColor(Color.BLACK);
            multirenderer.setYAxisColor(Color.BLACK);
            multirenderer.setXLabelsColor(Color.BLACK);
            multirenderer.setYLabelsColor(0,Color.BLACK);
            multirenderer.setMarginsColor(Color.WHITE);

        }
        Intent i;
        switch (grafica){
            case "Barras":
                i = ChartFactory.getBarChartIntent(Graficas.this, dataset, multirenderer, BarChart.Type.DEFAULT);
                break;
            case "Tarta":
                i = ChartFactory.getPieChartIntent(Graficas.this, mSeries, mRenderer, "AChartEnginePieChartDemo");
                break;
            default:
                i = ChartFactory.getLineChartIntent(Graficas.this, dataset, multirenderer);
                break;
        }
        startActivity(i);

    }
}
