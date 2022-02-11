fun makeCounter() {
  var i = 0;
  fun count() {
    i = i + 1;
    afiseaza i;
  }

  returneaza count;
}

var counter = makeCounter();
counter(); // "1".
counter(); // "2".