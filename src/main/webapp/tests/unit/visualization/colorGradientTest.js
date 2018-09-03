define(['colorGradient', 'color'], function(ColorGradient, Color) {
    describe("visualization/colorGradient", function() {
        var gradient;

        var redHex     = '#ff0000';
        var greenHex   = '#00ff00';
        var startColor = new Color(redHex);
        var endColor   = new Color(greenHex);

        describe("constructor", function() {
            it("instantiate", function() {
                gradient = new ColorGradient(startColor, endColor);
                expect(gradient.getStartColor()).toEqual(startColor);
                expect(gradient.getEndColor()).toEqual(endColor);
            });
        });

        describe("setters and getters", function() {
            var blueHex = '#0000ff';
            var color   = new Color(blueHex);

            beforeEach(function() {
                gradient = new ColorGradient(startColor, endColor);
            });

            it("set and get start color", function() {
                gradient.setStartColor(color);
                expect(gradient.getStartColor()).toEqual(color);
            });

            it("set and get end color", function() {
                gradient.setEndColor(color);
                expect(gradient.getEndColor()).toEqual(color);
            });
        });

        describe("methods", function() {
            var min = -50;
            var max = 50;
            var value;
            var color;

            beforeAll(function() {
                gradient = new ColorGradient(startColor, endColor);
            });

            it("getColor() color interpolation", function() {
                value = 25;
                color = new Color([64, 191, 0]);
                expect(gradient.getColor(min, max, value)).toEqual(color);
            });

            it("getColor() color interpolation with value smaller than min", function() {
                value = -75;
                color = startColor;
                expect(gradient.getColor(min, max, value)).toEqual(color);
            });

            it("getColor() color interpolation with value bigger than max", function() {
                value = 100;
                color = endColor;
                expect(gradient.getColor(min, max, value)).toEqual(color);
            });
        })
    });
});