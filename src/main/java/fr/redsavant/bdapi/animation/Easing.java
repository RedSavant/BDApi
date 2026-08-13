package fr.redsavant.bdapi.animation;

/**
 * Time interpolation function  apply(t) takes a
 * linear progression between 0 and 1 and returns the corresponding "curved" progression.
 */

public enum Easing {

    LINEAR {
        @Override
        public double apply(double t) {
            return t;
        }
    },
    EASE_IN {
        @Override
        public double apply(double t) {
            return t * t;
        }
    },
    EASE_OUT {
        @Override
        public double apply(double t) {
            return 1 - (1 - t) * (1 - t);
        }
    },
    EASE_IN_OUT {
        @Override
        public double apply(double t) {
            return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
        }
    },
    BOUNCE {
        @Override
        public double apply(double t) {
            final double n1 = 7.5625;
            final double d1 = 2.75;
            if (t < 1 / d1) {
                return n1 * t * t;
            } else if (t < 2 / d1) {
                t -= 1.5 / d1;
                return n1 * t * t + 0.75;
            } else if (t < 2.5 / d1) {
                t -= 2.25 / d1;
                return n1 * t * t + 0.9375;
            } else {
                t -= 2.625 / d1;
                return n1 * t * t + 0.984375;
            }
        }
    },
    ELASTIC {
        @Override
        public double apply(double t) {
            if (t == 0 || t == 1) return t;
            final double c4 = (2 * Math.PI) / 3;
            return Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
        }
    };

    public abstract double apply(double t);
}
