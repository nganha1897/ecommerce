var nextCount;
$(document).ready(function() {
	nextCount = $(".hiddenProductId").length;
	$("#products").on("click", "#linkAddProduct", function(e) {
		e.preventDefault();
		link = $(this);
		url = link.attr("href");

		$("#addProductModal").on("shown.bs.modal", function() {
			$(this).find("iframe").attr("src", url);
		});

		$("#addProductModal").modal();
	});
});

function addProduct(productId, productName) {
	getShippingCost(productId);
}

function getShippingCost(productId) {
	selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();

	state = $("#state").val();
	if (state.length == 0) {
		state = $("$city").val();
	}

	url = contextPath + "get_shipping_cost";
	params = { productId: productId, countryId: countryId, state: state };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: params,
	}).done(function(shippingCost) {
		getProductInfo(productId, shippingCost);
	}).fail(function(err) {
		showWarningModal(err.responseJSON.message);
		getProductInfo(productId, 0.0);
	}).always(function() {
		$("#addProductModal").modal("hide");
	});
}

function getProductInfo(productId, shippingCost) {
	url = contextPath + "products/get/" + productId;
	$.get(url, function(productJson) {
		console.log(productJson);
		productName = productJson.name;
		mainImagePath = contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
		cost = $.number(productJson.cost, 2);
		price = $.number(productJson.price, 2);
		htmlCode = insertProductCode(productId, productName, cost, mainImagePath, price, shippingCost);
		$("#productList").append(htmlCode);
		updateProductCountNumbers();
		updateOrderAmounts();
	}).fail(function(err) {
		showWarningModal(err.responseJson.message);
	});
}

function updateProductCountNumbers() {
	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	});
}

function insertProductCode(productId, productName, productCost, mainImagePath, productPrice, shippingCost) {
	nextCount++;
	quantityId = "quantity" + nextCount;
	priceId = "price" + nextCount;
	subtotalId = "subtotal" + nextCount;
	costId = "cost" + nextCount;
	rowId = "row" + nextCount;
	blankId = "blank" + nextCount;
	htmlCode = `
				<div class="border rounded p-1" id="${rowId}">
					<input type="hidden" name="productId" value="${productId}" class="hiddenProductId" />
					<input type="hidden" name="detailId" value="0" />
					<div class="row">
						<div class="col-1">
							<div class="divCount">${nextCount}</div>
							<div><a class="fas fa-trash icon-dark linkRemove" href=""
									rowNumber="${nextCount}"></a></div>
						</div>
						<div class="col-3">
							<img src="${mainImagePath}" class="img-fluid" />
						</div>
					</div>

					<div class="row m-2">
						<b>${productName}</b>
					</div>

					<div class="row m-2">
						<table>
							<tr>
								<td>Product Cost</td>
								<td>
									<input type="text" required class="form-control m-1 cost-input" name="productDetailCost"
									    rowNumber="${nextCount}" id="${costId}"
										value="${productCost}" style="max-width: 140px" />
								</td>
							</tr>
						</table>
					</div>

					<div class="row m-2">
						<table>
							<tr>
								<td>Quantity</td>
								<td>
									<input type="number" step="1" min="1" max="5" required name="quantity"
										class="form-control m-1 quantity-input" id="${quantityId}"
										rowNumber="${nextCount}" value="1"
										style="max-width: 140px" />
								</td>
							</tr>
						</table>
					</div>

					<div class="row m-2">
						<table>
							<tr>
								<td>Unit Price</td>
								<td>
									<input type="text" required class="form-control m-1 price-input" name="productPrice"
										id="${priceId}" value="${productPrice}" rowNumber="${nextCount}"
										style="max-width: 140px" />
								</td>
							</tr>
						</table>
					</div>

					<div class="row m-2">
						<table>
							<tr>
								<td>Subtotal</td>
								<td>
									<input type="text" class="form-control m-1 subtotal-output" readonly name="productSubtotal"
										id="${subtotalId}" value="${productPrice}"
										style="max-width: 140px" />
								</td>
							</tr>
						</table>
					</div>

					<div class="row m-2">
						<table>
							<tr>
								<td>Shipping Cost</td>
								<td>
									<input type="text" required class="form-control m-1 ship-input" name="shippingCost"
										value="${shippingCost}" style="max-width: 140px" />
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="row" id="${blankId}"}>&nbsp;</div>
	`;

	return htmlCode;
}

function isProductAlreadyAdded(productId) {
	productExists = false;
	$(".hiddenProductId").each(function(e) {
		curProductId = $(this).val();
		if (productId == curProductId) {
			productExists = true;
			return;
		}
	});

	return productExists;
}