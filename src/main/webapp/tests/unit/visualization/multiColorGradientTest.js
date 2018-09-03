define(['multiColorGradient', 'color'], function(MultiColorGradient, Color) {
    describe("visualization/multiColorGradient", function() {
        var multiColorGradient;
        
        var red    = new Color([255, 0, 0]);
        var green  = new Color([0, 255, 0]);
        var blue   = new Color([0, 0, 255]);
        var colors = [red, green, blue];

        describe("constructor", function() {
            it("instantiate", function() {
                multiColorGradient = new MultiColorGradient(colors);
                expect(multiColorGradient.getColors()).toEqual(colors);
            });
        });

        describe("setters and getters", function() {
            var white = new Color([255, 255, 255]);
            var newColors = [red, green, blue, white];

            beforeEach(function() {
                multiColorGradient = new MultiColorGradient(colors);
            });

            it("set and get colors", function() {
                multiColorGradient.setColors(newColors);
                expect(multiColorGradient.getColors()).toEqual(newColors);
            });
        });

        describe("methods", function() {
            var min = -50;
            var max = 50;
            var value;
            var color;

            beforeAll(function() {
                multiColorGradient = new MultiColorGradient(colors);
            });

            it("getColor() color interpolation", function() {
                value = 25;
                color = new Color([0, 128, 128]);
                expect(multiColorGradient.getColor(min, max, value)).toEqual(color);
            });

            it("getColor() color interpolation with value smaller than min", function() {
                value = -100;
                color = colors[0];
                expect(multiColorGradient.getColor(min, max, value)).toEqual(color);
            });

            it("getColor() color interpolation with value bigger than max", function() {
                value = 75;
                color = colors[colors.length - 1];
                expect(multiColorGradient.getColor(min, max, value)).toEqual(color);
            });
        });
    });
});