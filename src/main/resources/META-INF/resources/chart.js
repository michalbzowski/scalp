// Imports when using Browser Bundle
const {
    SciChartSurface,
    SciChartDefaults,
    chartBuilder,
    SciChartJsNavyTheme,
    XyDataSeries,
    FastLineRenderableSeries,
    NumericAxis
} = SciChart;

// Option 1: Create chart with Builder API
async function initSciChartBuilderApi() {
    // Create a chart using the json builder api
    await chartBuilder.buildChart("chart0", {
        series: {
            type: "LineSeries",
            options: { stroke: "steelblue", strokeThickness: 5 },
            xyData: {
                xValues: [1, 2, 5, 8, 10],
                yValues: [3, 1, 7, 5, 8]
            }
        }
    });
}

// Option 2: Create chart with the programmatic API
async function initSciChartProgrammaticApi() {
    const { sciChartSurface, wasmContext } = await SciChartSurface.create("chart1", {
        theme: new SciChartJsNavyTheme()
    });

    sciChartSurface.xAxes.add(new NumericAxis(wasmContext));
    sciChartSurface.yAxes.add(new NumericAxis(wasmContext));

    sciChartSurface.renderableSeries.add(
        new FastLineRenderableSeries(wasmContext, {
            stroke: "#FF6600",
            strokeThickness: 3,
            dataSeries: new XyDataSeries(wasmContext, {
                xValues: [1, 2, 5, 8, 10],
                yValues: [3, 1, 7, 5, 8]
            })
        })
    );
}

// See deployment options for WebAssembly at https://www.scichart.com/documentation/js/current/Deploying%20Wasm%20or%20WebAssembly%20and%20Data%20Files%20with%20your%20app.html
// call useWasmFromCDN once before SciChart.js is initialised to load Wasm files from our CDN
SciChartSurface.useWasmFromCDN();
// Also, call & set runtime license key here once before scichart shown
SciChartSurface.setRuntimeLicenseKey("-- Your license key here --");

initSciChartBuilderApi();
initSciChartProgrammaticApi();