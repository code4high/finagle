package com.twitter.finagle.stats

import org.scalatest.FunSuite

class MetricsStatsReceiverTest extends FunSuite {
  private[this] val rootReceiver = new MetricsStatsReceiver()

  private[this] def readGauge(metrics: MetricsStatsReceiver, name: String): Number =
    metrics.registry.gauges.get(name)

  private[this] def readGaugeInRoot(name: String) = readGauge(rootReceiver, name)
  private[this] def readCounterInRoot(name: String) = rootReceiver.registry.counters.get(name)

  test("MetricsStatsReceiver should store and read gauge into the root StatsReceiver") {
    val x = 1.5f
    // gauges are weakly referenced by the registry so we need to keep a strong reference
    val g = rootReceiver.addGauge("my_gauge")(x)
    assert(readGaugeInRoot("my_gauge") == x)
  }

  test("cumulative gauge is working") {
    val x = 1
    val y = 2
    val z = 3
    val g1 = rootReceiver.addGauge("my_cumulative_gauge")(x)
    val g2 = rootReceiver.addGauge("my_cumulative_gauge")(y)
    val g3 = rootReceiver.addGauge("my_cumulative_gauge")(z)
    assert(readGaugeInRoot("my_cumulative_gauge") == x + y + z)
  }

  test("Ensure that we throw an exception with a counter and a gauge when rollup collides") {
    val sr = new RollupStatsReceiver(rootReceiver)
    sr.counter("a", "b", "c").incr()
    intercept[MetricCollisionException] {
      sr.addGauge("a", "b", "d") { 3 }
    }
  }

  test("Ensure that we throw an exception when rollup collides via scoping") {
    val sr = new RollupStatsReceiver(rootReceiver)
    val newSr = sr.scope("a").scope("b")
    newSr.counter("c").incr()
    intercept[MetricCollisionException] {
      newSr.addGauge("d") { 3 }
    }
  }

  test("toString") {
    val sr = new MetricsStatsReceiver(new Metrics())
    assert("MetricsStatsReceiver" == sr.toString)
    assert("MetricsStatsReceiver/s1" == sr.scope("s1").toString)
    assert("MetricsStatsReceiver/s1/s2" == sr.scope("s1").scope("s2").toString)
  }

  test("reading histograms initializes correctly") {
    val sr = new MetricsStatsReceiver(new Metrics())
    val stat = sr.stat("my_cool_stat")

    val reader = sr.registry.histoDetails.get("my_cool_stat")
    assert(reader != null && reader.counts == Nil)
  }

  test("store and read counter into the root StatsReceiver") {
    rootReceiver.counter("my_counter").incr()
    assert(readCounterInRoot("my_counter") == 1)
  }

  test("separate gauge/stat/metric between detached Metrics and root Metrics") {
    val detachedReceiver = new MetricsStatsReceiver(new Metrics())
    val g1 = detachedReceiver.addGauge("xxx")(1.0f)
    val g2 = rootReceiver.addGauge("xxx")(2.0f)
    assert(readGauge(detachedReceiver, "xxx") != readGauge(rootReceiver, "xxx"))
  }
}
