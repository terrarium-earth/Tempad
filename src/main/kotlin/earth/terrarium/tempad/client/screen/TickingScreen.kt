package earth.terrarium.tempad.client.screen

interface TickingScreen {
    val tickers: MutableList<() -> Unit>
}