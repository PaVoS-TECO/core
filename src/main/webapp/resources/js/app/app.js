define(['initializationRoutine', 'fetchRoutine'], function(InitializationRoutine, FetchRoutine) {
    function App() { }

    App.prototype.run = function() {
        initRoutine = new InitializationRoutine(FetchRoutine.start);
        initRoutine.run();
    }

    return App;
});