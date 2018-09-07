package it.trade.android.sdk

import it.trade.android.sdk.manager.TradeItLinkedBrokerManagerTest
import it.trade.android.sdk.model.*
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(TradeItLinkedBrokerManagerTest::class,
        TradeItErrorResultParcelableTest::class,
        TradeItLinkedBrokerAccountParcelableTest::class,
        TradeItLinkedBrokerParcelableTest::class,
        TradeItOrderCapabilityParcelableTest::class,
        TradeItOrderParcelableTest::class,
        TradeItOrderStatusParcelableTest::class,
        TradeItPlaceStockOrEtfOrderResponseParcelableTest::class,
        TradeItPreviewStockOrEtfOrderResponseParcelableTest::class,
        TradeItSecurityQuestionParcelableTest::class)
class UnitTestSuite