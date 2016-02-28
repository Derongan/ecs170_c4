//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class alphabeta_zugzwang extends AIModule {
    private final int MAXVAL = 99999;
    private final int MINVAL = -99999;
    private final int NOMOVE = -1;
    private int ourPlayer;
    private int opponent;
    private int[][][] winningLines = new int[][][]{{{0, 0}, {0, 1}, {0, 2}, {0, 3}}, {{0, 1}, {0, 2}, {0, 3}, {0, 4}}, {{0, 2}, {0, 3}, {0, 4}, {0, 5}}, {{1, 0}, {1, 1}, {1, 2}, {1, 3}}, {{1, 1}, {1, 2}, {1, 3}, {1, 4}}, {{1, 2}, {1, 3}, {1, 4}, {1, 5}}, {{2, 0}, {2, 1}, {2, 2}, {2, 3}}, {{2, 1}, {2, 2}, {2, 3}, {2, 4}}, {{2, 2}, {2, 3}, {2, 4}, {2, 5}}, {{3, 0}, {3, 1}, {3, 2}, {3, 3}}, {{3, 1}, {3, 2}, {3, 3}, {3, 4}}, {{3, 2}, {3, 3}, {3, 4}, {3, 5}}, {{4, 0}, {4, 1}, {4, 2}, {4, 3}}, {{4, 1}, {4, 2}, {4, 3}, {4, 4}}, {{4, 2}, {4, 3}, {4, 4}, {4, 5}}, {{5, 0}, {5, 1}, {5, 2}, {5, 3}}, {{5, 1}, {5, 2}, {5, 3}, {5, 4}}, {{5, 2}, {5, 3}, {5, 4}, {5, 5}}, {{6, 0}, {6, 1}, {6, 2}, {6, 3}}, {{6, 1}, {6, 2}, {6, 3}, {6, 4}}, {{6, 2}, {6, 3}, {6, 4}, {6, 5}}, {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, {{1, 0}, {2, 0}, {3, 0}, {4, 0}}, {{2, 0}, {3, 0}, {4, 0}, {5, 0}}, {{3, 0}, {4, 0}, {5, 0}, {6, 0}}, {{0, 1}, {1, 1}, {2, 1}, {3, 1}}, {{1, 1}, {2, 1}, {3, 1}, {4, 1}}, {{2, 1}, {3, 1}, {4, 1}, {5, 1}}, {{3, 1}, {4, 1}, {5, 1}, {6, 1}}, {{0, 2}, {1, 2}, {2, 2}, {3, 2}}, {{1, 2}, {2, 2}, {3, 2}, {4, 2}}, {{2, 2}, {3, 2}, {4, 2}, {5, 2}}, {{3, 2}, {4, 2}, {5, 2}, {6, 2}}, {{0, 3}, {1, 3}, {2, 3}, {3, 3}}, {{1, 3}, {2, 3}, {3, 3}, {4, 3}}, {{2, 3}, {3, 3}, {4, 3}, {5, 3}}, {{3, 3}, {4, 3}, {5, 3}, {6, 3}}, {{0, 4}, {1, 4}, {2, 4}, {3, 4}}, {{1, 4}, {2, 4}, {3, 4}, {4, 4}}, {{2, 4}, {3, 4}, {4, 4}, {5, 4}}, {{3, 4}, {4, 4}, {5, 4}, {6, 4}}, {{0, 5}, {1, 5}, {2, 5}, {3, 5}}, {{1, 5}, {2, 5}, {3, 5}, {4, 5}}, {{2, 5}, {3, 5}, {4, 5}, {5, 5}}, {{3, 5}, {4, 5}, {5, 5}, {6, 5}}, {{0, 2}, {1, 3}, {2, 4}, {3, 5}}, {{0, 1}, {1, 2}, {2, 3}, {3, 4}}, {{1, 2}, {2, 3}, {3, 4}, {4, 5}}, {{0, 0}, {1, 1}, {2, 2}, {3, 3}}, {{1, 1}, {2, 2}, {3, 3}, {4, 4}}, {{2, 2}, {3, 3}, {4, 4}, {5, 5}}, {{1, 0}, {2, 1}, {3, 2}, {4, 3}}, {{2, 1}, {3, 2}, {4, 3}, {5, 4}}, {{3, 2}, {4, 3}, {5, 4}, {6, 5}}, {{2, 0}, {3, 1}, {4, 2}, {5, 3}}, {{3, 1}, {4, 2}, {5, 3}, {6, 4}}, {{3, 0}, {4, 1}, {5, 2}, {6, 3}}, {{0, 3}, {1, 2}, {2, 1}, {3, 0}}, {{0, 4}, {1, 3}, {2, 2}, {3, 1}}, {{1, 3}, {2, 2}, {3, 1}, {4, 0}}, {{0, 5}, {1, 4}, {2, 3}, {3, 2}}, {{1, 4}, {2, 3}, {3, 2}, {4, 1}}, {{2, 3}, {3, 2}, {4, 1}, {5, 0}}, {{1, 5}, {2, 4}, {3, 3}, {4, 2}}, {{2, 4}, {3, 3}, {4, 2}, {5, 1}}, {{3, 3}, {4, 2}, {5, 1}, {6, 0}}, {{2, 5}, {3, 4}, {4, 3}, {5, 2}}, {{3, 4}, {4, 3}, {5, 2}, {6, 1}}, {{3, 5}, {4, 4}, {5, 3}, {6, 2}}};

    public alphabeta_zugzwang() {
    }

    public void getNextMove(GameStateModule var1) {
        int var2;
        for(var2 = 0; var2 < var1.getWidth(); ++var2) {
            if(var1.canMakeMove(var2)) {
                this.chosenMove = var2;
                break;
            }
        }

        this.ourPlayer = var1.getActivePlayer();
        if(this.ourPlayer == 1) {
            this.opponent = 2;
        } else {
            this.opponent = 1;
        }

        if(var1.getCoins() == 0 && this.ourPlayer == 1) {
            this.chosenMove = 3;
        } else {
            var2 = 3;
            boolean var3 = false;
            boolean var4 = false;

            try {
                while(!this.terminate && var2 < 42) {
                    int[] var5 = this.abNegamax(true, var1, var2, 0, -2147483647, 2147483647);
                    int var7 = var5[0];
                    if(var7 == -99999 && var4) {
                        break;
                    }

                    this.chosenMove = var5[1];
                    var4 = true;
                    ++var2;
                }

            } catch (Exception var6) {
            }
            //System.out.println("Max reached depth " + String.valueOf(var2 - 1) + " by player " + String.valueOf(ourPlayer));

        }
    }

    public int[] abNegamax(boolean var1, GameStateModule var2, int var3, int var4, int var5, int var6) {
        if(this.terminate) {
            throw new RuntimeException("Out of time");
        } else if(!var2.isGameOver() && var4 != var3) {
            int var7 = -1;
            int var8 = -2147483647;
            boolean var9 = false;
            int[] var11 = new int[]{3, 2, 4, 1, 5, 0, 6};

            for(int var12 = 0; var12 < var2.getWidth(); ++var12) {
                if(var2.canMakeMove(var11[var12])) {
                    var2.makeMove(var11[var12]);
                    int[] var10 = this.abNegamax(!var1, var2, var3, var4 + 1, -var6, -Math.max(var5, var8));
                    var2.unMakeMove();
                    int var13 = -var10[0];
                    if(var13 > var8) {
                        var8 = var13;
                        var7 = var11[var12];
                        if(var13 >= var6) {
                            return new int[]{var13, var7};
                        }
                    }
                }
            }

            return new int[]{var8, var7};
        } else {
            return var1?new int[]{this.utility(var2), -1}:new int[]{-this.utility(var2), -1};
        }
    }

    public int utility(GameStateModule var1) {
        if(this.terminate) {
            throw new RuntimeException("Out of time");
        } else {
            int var2 = 0;
            if(var1.isGameOver()) {
                return var1.getWinner() == this.ourPlayer?99999:(var1.getWinner() != 0?-99999:0);
            } else {
                int var3 = var1.getActivePlayer();
                int[][] var4 = new int[][]{{0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}};
                int[][][] var5 = this.winningLines;
                int var6 = var5.length;

                int var7;
                int var9;
                int var12;
                for(var7 = 0; var7 < var6; ++var7) {
                    int[][] var8 = var5[var7];
                    var9 = 0;
                    int var10 = 0;
                    int var11 = 0;
                    var12 = 0;
                    int[][] var13 = var8;
                    int var14 = var8.length;

                    for(int var15 = 0; var15 < var14; ++var15) {
                        int[] var16 = var13[var15];
                        int var17 = var1.getAt(var16[0], var16[1]);
                        if(var17 == this.ourPlayer) {
                            ++var9;
                        } else if(var17 != 0) {
                            ++var10;
                        } else {
                            var11 = var16[0];
                            var12 = var16[1];
                        }
                    }

                    if(var9 <= 0 || var10 <= 0) {
                        if(var9 == 3) {
                            if(var12 > 0 && var1.getAt(var11, var12 - 1) == 0) {
                                if(var4[var11][var12] == this.opponent) {
                                    var4[var11][var12] = 3;
                                } else {
                                    var4[var11][var12] = this.ourPlayer;
                                }

                                if(this.ourPlayer == 1) {
                                    if(var12 % 2 == 0) {
                                        var2 += 10;
                                    }
                                } else if(var12 % 2 == 1) {
                                    var2 += 10;
                                }
                            } else if(var3 == this.ourPlayer) {
                                return 99999;
                            }
                        } else if(var10 == 3) {
                            if(var12 > 0 && var1.getAt(var11, var12 - 1) == 0) {
                                if(var4[var11][var12] == this.ourPlayer) {
                                    var4[var11][var12] = 3;
                                } else {
                                    var4[var11][var12] = this.opponent;
                                }

                                if(this.ourPlayer == 1) {
                                    if(var12 % 2 == 1) {
                                        var2 -= 10;
                                    }
                                } else if(var12 % 2 == 0) {
                                    var2 -= 10;
                                }
                            } else if(var3 != this.ourPlayer) {
                                return -99999;
                            }
                        }

                        var2 += 1 * (var9 - var10);
                    }
                }

                int var18 = 0;
                var6 = 0;
                var7 = 0;
                int var19 = 0;

                for(var9 = 0; var9 < 7; ++var9) {
                    boolean var20 = false;
                    boolean var21 = false;

                    for(var12 = 0; var12 < 6; ++var12) {
                        if(var4[var9][var12] == 1) {
                            if(var12 % 2 == 0 && !var21) {
                                ++var18;
                                var20 = true;
                            }
                        } else if(var4[var9][var12] == 2) {
                            if(var12 % 2 == 1) {
                                ++var19;
                                var21 = true;
                            } else {
                                ++var7;
                                if(!var20) {
                                    ++var6;
                                }
                            }
                        } else if(var4[var9][var12] == 3) {
                            if(var12 % 2 == 1) {
                                var21 = true;
                            } else if(!var21) {
                                ++var18;
                                var20 = true;
                            }
                        }
                    }
                }

                if(var18 > 0 && var6 == 0) {
                    if(this.ourPlayer == 1) {
                        var2 += 100;
                    } else {
                        var2 -= 100;
                    }
                } else if(var18 > var7 && var19 == 0) {
                    if(this.ourPlayer == 1) {
                        var2 += 100;
                    } else {
                        var2 -= 100;
                    }
                } else if(var19 > 0) {
                    if(this.ourPlayer == 1) {
                        var2 -= 100;
                    } else {
                        var2 += 100;
                    }
                }

                return var2;
            }
        }
    }
}
