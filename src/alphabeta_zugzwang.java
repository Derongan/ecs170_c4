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

    public int utility(GameStateModule state) {
        if(this.terminate) {
            throw new RuntimeException("Out of time");
        } else {
            int util = 0;
            if(state.isGameOver()) {
                return state.getWinner() == this.ourPlayer?99999:(state.getWinner() != 0?-99999:0);
            } else {
                int who = state.getActivePlayer();
                int[][] board = new int[][]{{0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}};
                int[][][] winningpositions = this.winningLines;
                int numberWinningPositions = winningpositions.length;

                int i;
                int ours;
                int y;
                for(i = 0; i < numberWinningPositions; ++i) {
                    int[][] winpos = winningpositions[i];
                    ours = 0;
                    int theirs = 0;
                    int x = 0;
                    y = 0;
                    int[][] winpos2 = winpos;
                    int willBe4 = winpos.length;


                    //Find the threat location
                    for(int j = 0; j < willBe4; ++j) {
                        int[] squareLoc = winpos2[j];
                        int playerNum = state.getAt(squareLoc[0], squareLoc[1]);
                        if(playerNum == this.ourPlayer) {
                            ++ours;
                        } else if(playerNum != 0) {
                            ++theirs;
                        } else {
                            x = squareLoc[0];
                            y = squareLoc[1];
                        }
                    }

                    //If only one of us controls this win position
                    if(ours <= 0 || theirs <= 0) {
                        //If we control it
                        if(ours == 3) {
                            if(y > 0 && state.getAt(x, y - 1) == 0) {
                                if(board[x][y] == this.opponent) {
                                    board[x][y] = 3;
                                } else {
                                    board[x][y] = this.ourPlayer;
                                }

                                //If we are first player we want odd! Otherwise we want Even!
                                if(this.ourPlayer == 1) {
                                    if(y % 2 == 0) {
                                        util += 10; //If its odd we can probably take it if we are p1
                                    }
                                } else if(y % 2 == 1) {
                                    util += 10; //If its even we can probably take it if we are p2
                                }
                            } else if(who == this.ourPlayer) {
                                return 99999;
                            }
                        }
                        //If they control it
                        else if(theirs == 3) {
                            //If the slot below it is empty
                            if(y > 0 && state.getAt(x, y - 1) == 0) {
                                if(board[x][y] == this.ourPlayer) {
                                    board[x][y] = 3;
                                } else {
                                    board[x][y] = this.opponent;
                                }

                                if(this.ourPlayer == 1) {
                                    if(y % 2 == 1) {
                                        util -= 10; //If its odd
                                    }
                                } else if(y % 2 == 0) {
                                    util -= 10; //If y is even???
                                }
                            } else if(who != this.ourPlayer) {
                                return -99999;
                            }
                        }

                        util += 1 * (ours - theirs);    //We also add the number we have in that slot
                    }
                }

                int myOdd = 0;
                int idk = 0;
                int oddShared = 0;
                int notmine = 0;

                for(int x = 0; x < 7; ++x) {
                    boolean seenOddMe = false;
                    boolean seenEvenThem = false;

                    for(y = 0; y < 6; ++y) {
                        //Me
                        if(board[x][y] == 1) {
                            if(y % 2 == 0 && !seenEvenThem) {
                                ++myOdd;
                                seenOddMe = true;
                            }
                        //You
                        } else if(board[x][y] == 2) {
                            if(y % 2 == 1) {
                                ++notmine;
                                seenEvenThem = true;
                            } else {
                                ++oddShared;
                                if(!seenOddMe) {
                                    ++idk;
                                }
                            }
                        //US
                        } else if(board[x][y] == 3) {
                            if(y % 2 == 1) {
                                seenEvenThem = true;
                            } else if(!seenEvenThem) {
                                ++myOdd;
                                seenOddMe = true;
                            }
                        }
                    }
                }

                if(myOdd > 0 && idk == 0) {
                    if(this.ourPlayer == 1) {
                        util += 100;
                    } else {
                        util -= 100;
                    }
                } else if(myOdd > oddShared && notmine == 0) {
                    if(this.ourPlayer == 1) {
                        util += 100;
                    } else {
                        util -= 100;
                    }
                } else if(notmine > 0) {
                    if(this.ourPlayer == 1) {
                        util -= 100;
                    } else {
                        util += 100;
                    }
                }

                return util;
            }
        }
    }
}
